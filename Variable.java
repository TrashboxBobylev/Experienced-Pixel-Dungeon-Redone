package com.zrp200.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

abstract public class Variable<T> {
    private static HashMap<String, Variable> all = new HashMap();

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

    static boolean put(String key, Object o) {
        if (key == null) return true; // no variable to store.
        if (o instanceof Actor) {
            all.put(key, new ActorVariable((Actor) o));
            return true;
        }
        else all.put(key, new ObjectVariable(o));
        return true;
    }

    static Object get(String key) {
        if(key.startsWith("@")) {
            Variable v = getActive().get(key.substring(1));
            if(v != null) return v.getTarget();
        }
        return null;
    }
    static <T> T get(String key, Class<T> expectedClass) {
        Object o = get(key);
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
        return target.getClass().getSimpleName();
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
            return objectAsString + " (" + id + ")";
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
