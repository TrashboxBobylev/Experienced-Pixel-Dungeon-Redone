package com.zrp200.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

abstract public class Variable<T> {
    private static final HashMap<String, Variable> all = new HashMap();

    public static final String MARKER = "@";

    static void putFromInventory(String key) {
        GameScene.selectItem(new WndBag.ItemSelector() {
            @Override
            public String textPrompt() { return "Select an item"; }
            @Override
            public boolean itemSelectable(Item item) { return !(item instanceof ScrollOfDebug); }
            @Override
            public void onSelect(Item item) { put(key, item); }
        });
    }

    static void putFromCell(String key) {
        GameScene.selectCell(new CellSelector.Listener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSelect(Integer cell) {
                if (cell == null || cell == -1) return;
                // determine objects of interest.
                // find stuff of interest
                // accessing private methods :/
                final ArrayList<Object> objects = new ArrayList<>();
                final ArrayList<String> objectNames = new ArrayList<>();
                try {
                    Method
                            getObjectsAtCell = GameScene.class.getDeclaredMethod("getObjectsAtCell", int.class),
                            getObjectNames = GameScene.class.getDeclaredMethod("getObjectNames", ArrayList.class);
                    // getting around the private declaration
                    // fixme this may not work properly on mobile.
                    getObjectNames.setAccessible(true);
                    getObjectsAtCell.setAccessible(true);
                    // now using the functions
                    objects.addAll((ArrayList<Object>) getObjectsAtCell.invoke(null, cell));
                    objectNames.addAll((ArrayList<String>) getObjectNames.invoke(null, objects));
                } catch (Exception e) {
                    // maybe copy paste the shattered implementation?
                    // currently we just pretend nothing else is there.
                    Game.reportException(e);
                }
                if (objects.isEmpty()) {
                    put(key, cell);
                } else {
                    objects.add(0, cell);
                    objectNames.add(0, "cell (" + cell + ")"); // include the actual value of the cell
                    GameScene.show(new WndOptions(Messages.get(GameScene.class, "multiple"),
                            "", objectNames.toArray(new String[0])) {
                        @Override protected void onSelect(int index) { put(key, objects.get(index)); }
                    });
                }
            }

            @Override public String prompt() { return "Choose a location to target"; }
        });
    }

    static void put(String key, Object o) {
        if (key == null) return; // no variable to store.
        Variable variable;
        if (o instanceof Actor) {
            all.put(key, variable = new ActorVariable((Actor) o));
        } else all.put(key, variable = new ObjectVariable(o));
        GLog.p("%s = %s", key, variable);
    }

    static Object get(String key) {
        if(key.startsWith(MARKER)) {
            Variable v = getActive().get(key);
            if(v != null) return v.getTarget();
        }
        return null;
    }

    // note this can still have situational weird behavior, but this should mostly prevent unintentional variable usage.
    static <T> T get(String key, Class<? super T> expectedClass) {
        Object o = get(key);
        if(expectedClass.isPrimitive()) {
            // wrap it.
            Class<?> wrapper;
            if(expectedClass == int.class) wrapper = Integer.class;
            else if(expectedClass == char.class) wrapper = Character.class;
            else try {
                // all other wrappers are just capitalized primitives
                wrapper = Class.forName("java.lang." + Messages.capitalize(expectedClass.getSimpleName()));
            } catch (Exception e) { wrapper = expectedClass; }
            //change expected class to the wrapper.
            //noinspection unchecked
            expectedClass = (Class<? super T>)wrapper;
        }
        //noinspection unchecked
        return expectedClass.isInstance(o) ? (T)o : null;
    }

    public static HashMap<String, Variable> getActive() {
        HashMap<String, Variable> active = new HashMap(all);
        {
            Iterator<Map.Entry<String, Variable>> entries = all.entrySet().iterator();
            Map.Entry<?, Variable> entry;
            while (entries.hasNext()) {
                entry = entries.next();
                if (!entry.getValue().isActive()) {
                    entries.remove();
                }
            }
        }
        return active;
    }

    abstract T getTarget();

    boolean isActive() {
        return getTarget() != null;
    }

    @Override
    public int hashCode() {
        T target = getTarget();
        return target != null ? target.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        T target = getTarget();
        if (target == null) return null;
        Class c = target.getClass();
        String objectAsString = c.getSimpleName();
        try {
            // if we have a pretty toString, use that instead.
            if (c.isPrimitive() || c.getMethod("toString").getDeclaringClass() != Object.class) {
                objectAsString = target.toString();
            }
        } catch (NoSuchMethodException e) {/*impossible*/}
        return objectAsString;
    }

//    @Override
//    public void storeInBundle(Bundle bundle) {}
//
//    @Override
//    public void restoreFromBundle(Bundle bundle) {}

    public static class ObjectVariable extends Variable<Object> {
        private final Object target;
        ObjectVariable(Object target) {
            this.target = target;
        }
        @Override
        Object getTarget() {
            return target;
        }
    }

    public static class ActorVariable extends Variable<Actor> {
        private int id, depth;

        @Deprecated
        ActorVariable() {} // for Bundling only

        ActorVariable(Actor actor) {
            id = actor.id();
            depth = Dungeon.depth;
        }

        @Override
        Actor getTarget() { return Actor.findById(id); }

        @Override
        public String toString() {
            Actor target = getTarget();
            if (target == null) return null;
            Class c = target.getClass();
            String objectAsString = c.getSimpleName();
            try {
                // if we have a pretty toString, use that instead.
                if(c.getMethod("toString").getDeclaringClass() != Object.class) {
                    objectAsString = target.toString();
                }
            } catch (NoSuchMethodException e) {/*impossible*/}
            return objectAsString + " (id=" + id + ")";
        }

//        @Override
//        public void storeInBundle(Bundle bundle) {
//            bundle.put("id", id);
//            bundle.put("depth", depth);
//        }
//
//        @Override
//        public void restoreFromBundle(Bundle bundle) {
//            id = bundle.getInt("id");
//            depth = bundle.getInt("depth");
//        }
    }
}
