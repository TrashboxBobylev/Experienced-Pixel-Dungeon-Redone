package com.zrp200.scrollofdebug;

import static java.util.Arrays.copyOfRange;

import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.StringBuilder;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
// Commands
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
// needed for HelpWindow
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
// WndTextInput (added in v0.9.4)
import com.shatteredpixel.shatteredpixeldungeon.ui.WndTextInput;
// Output
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import com.watabou.noosa.ui.Component;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;
import com.zrp200.scrollofdebug.PackageTrie;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Scroll of Debug uses ClassLoader to get every class that can be directly created and provides a command interface with which to interact with them.
 *
 * @author  <a href="https://github.com/zrp200/scrollofdebug">
 *              Zrp200
 * @version v0.3.0
 * **/
@SuppressWarnings({"rawtypes", "unchecked"})
public class ScrollOfDebug extends Scroll {
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
    }

    /** this is where all the game files are supposed to be located. **/
    private static final String ROOT = "com.shatteredpixel.shatteredpixeldungeon";

    private enum Command {
        HELP(null, // ...
                "[COMMAND | all]",
                "Gives more information on commands, or in the case of 'all', all of them."),
        // todo add more debug-oriented commands
        // generation commands.
        GIVE(Item.class,
                "ITEM [_+_LEVEL] [_x_QUANTITY] [-f | --force] [ METHOD [args] ]",
                "Creates and puts into your inventory the generated item."),
        SPAWN(Mob.class,
                "MOB",
                "Summons the indicated mob and randomly places them on the depth."),
        SET(Trap.class,
                "TRAP",
                "Sets a trap at an indicated position"),
        AFFECT(Buff.class,
                "BUFF [duration] [METHOD [args]]",
                "Allows you to attach a buff to a character in sight. This can be extremely dangerous, or it could do literally nothing."),
        SEED(Blob.class,
                "BLOB [amount]",
                "Seeds a blob of the specified amount to a targeted tile"),
        INSPECT(Object.class, "CLASS", "Gives a list of supported methods for the indicated class.") {
            @Override String fullDocumentation(PackageTrie trie) {
                return documentation(); // absolutely not.
            }
        };

        final Class<?> paramClass;
        final String syntax, description;
        Command(Class<?> paramClass, String syntax, String description) {
            this.paramClass = paramClass;
            this.syntax = syntax;
            this.description = description;
        }

        @Override public String toString() { return name().toLowerCase(); }

        String documentation() { return documentation(this, syntax, description); }
        static String documentation(Object command, String syntax, String description) {
            return String.format("_%s_ %s\n%s", command, syntax, description);
        }

        // adds more information depending on what the paramClass actually is.
        String fullDocumentation(PackageTrie trie) {
            String documentation = documentation();
            if(paramClass != null) {
                documentation += "\n\n_Valid Classes_:" + listAllClasses(trie,paramClass);
            }
            return documentation;
        }


        static Command get(String string) { try {
            return valueOf(string.toUpperCase());
        } catch (Exception e) { return null; } }
    }

    @Override
    public void doRead() {
        collect(); // you don't lose scroll of debug.
        GameScene.show(new WndTextInput("Enter Command:", "", 100, false,
                "Execute", "Cancel") {
            @Override public void onSelect(boolean positive, String text) {
                if(!positive) return;

                String[] input = text.split(" ");
                Callback init = null;

                Command command; try { command = Command.valueOf(input[0].toUpperCase()); }
                catch (Exception e) {
                    GLog.w("\""+input[0]+"\" is not a valid command.");
                    return;
                }

                if(command == Command.HELP) {
                    String output = null;
                    boolean all = false;
                    if (input.length > 1) {
                        // we only care about the initial argument.
                        Command cmd = Command.get(input[1]);
                        if (cmd != null) output = cmd.fullDocumentation(trie);
                        else all = input[1].equalsIgnoreCase("all");
                    }
                    if (output == null) {
                        StringBuilder builder = new StringBuilder();
                        for (Command cmd : Command.values()) {
                            if (all) {
                                // extensive. help is omitted because we are using help.
                                if (cmd != Command.HELP) {
                                    builder.append("\n\n").append(cmd.fullDocumentation(trie));
                                }
                            } else {
                                // by default attempt to shorten the command list as much as possible.
                                builder.append(String.format("\n_%s_: %s", cmd, cmd.description));
                            }
                        }
                        output = builder.toString().trim();
                    }
                    GameScene.show(new HelpWindow(output));
                } else if(input.length > 1) {
                    Class _cls = trie.findClass(input[1], command.paramClass);

                    if(command == Command.INSPECT) {
                        Class cls = _cls;
                        if(cls == null) {
                            Command c = Command.get(input[1]);
                            if(c != null) cls = c.paramClass;
                        }
                        if(cls != null) {
                            StringBuilder message = new StringBuilder();
                            for(Map.Entry<Class,Set<Method>> entry : hierarchy(cls).entrySet()) {
                                String className = entry.getKey().getName();
                                int i = className.indexOf(ROOT);
                                if(i == -1) continue;
                                className = className.substring(i+ROOT.length()+1);
                                message.append("\n\n_").append(className).append("_");
                                for(Method m : entry.getValue()) {
                                    message.append("\n_").append(Modifier.isStatic(m.getModifiers()) ? '*' : '-').append("_")
                                            .append(m.getName());
                                    for(Class p : m.getParameterTypes()) {
                                        if(p == Hero.class) continue;
                                        message.append(' ').append(p.getSimpleName().toUpperCase());
                                    }
                                }
                            }
                            GameScene.show(new HelpWindow(
                                    "inspection of _"+input[1]+"_:"
                                            + message.toString() ));
                            return;
                        }
                    }

                    final Class cls = _cls;

                    boolean valid = true;
                    Object o = null; try {
                        o = Reflection.newInstanceUnhandled(cls);
                    } catch (Exception e) { valid = false; }
                    if (valid) switch (command) {
                        case SPAWN: Mob mob = (Mob)o;
                            mob.pos = Dungeon.level.randomRespawnCell(mob);
                            if(mob.pos != 1) {
                                GameScene.add(mob);
                                GLog.w("Summoned " + mob.name());
                            }
                            break;
                        case SET:
                            Trap t = (Trap)o;
                            GameScene.selectCell(new CellSelector.Listener() {
                                @Override
                                public void onSelect(Integer cell) {
                                    if(cell ==  null || cell == -1) return;
                                    // currently manually set traps are always revealed.
                                    Dungeon.level.setTrap(t.set(cell).reveal(), cell);
                                    Level.set(cell, Terrain.TRAP);
                                }
                                @Override public String prompt() {
                                    return "Select location of trap:";
                                }
                            });
                            break;
                        case GIVE: Item item = (Item)o;
                            item.identify();
                            // todo add enchants/glyphs for weapons/armor?
                            // process modifiers left to right (so later ones have higher precedence)
                            boolean collect = false;
                            int last = 1;
                            for(int i=2; i < input.length; i++) try {
                                if(input[i].startsWith("--force") || input[i].equalsIgnoreCase("-f")) {
                                    collect = true;
                                    last = i;
                                }
                                else {
                                    int number = Integer.parseInt(input[i].substring(1));
                                    switch(input[i].toLowerCase(Locale.ENGLISH).charAt(0)) {
                                        case 'x':
                                            item.quantity(number);
                                            last = i;
                                            break;
                                        case '+':
                                            item.level(number);
                                            last = i;
                                            break;
                                    }
                                }
                            } catch (NumberFormatException e) {/* do nothing */}
                            if(++last < input.length) executeMethod(input[last++],
                                    item, copyOfRange(input, last, input.length));
                            Item toPickUp = collect ? new Item() {
                                // create wrapper item that simulates doPickUp while actually just calling collect.
                                { image = item.image; }
                                @Override public boolean collect(Bag container) {
                                    return item.collect(container);
                                }
                            } : item;
                            if (toPickUp.doPickUp(curUser)) {
                                // ripped from Hero#actPickUp, kinda.
                                boolean important = item.unique && (item instanceof Scroll || item instanceof Potion);
                                String pickupMessage = Messages.get(curUser, "you_now_have", item);
                                if(important) GLog.p(pickupMessage); else GLog.i(pickupMessage);
                                // attempt to nullify turn usage.
                                curUser.spend(-curUser.cooldown());
                            }
                            // worth a shot.
                            break;
                        case AFFECT:
                            Buff buff = (Buff)o;
                            // fixme perhaps have special logic for when additional arguments in general are passed to non-flavor buffs.
                            GameScene.selectCell(new CellSelector.Listener() {
                                @Override public String prompt() {
                                    return "Select the character to apply the buff to:";
                                }
                                @Override public void onSelect(Integer cell) {
                                    Char target;
                                    if(cell == null || cell == -1 || (target = Actor.findChar(cell)) == null) return;
                                    Buff added = null;
                                    int index = 2;

                                    boolean success = false;

                                    if(index >= input.length)
                                    {
                                        // no additional arguments.
                                        Buff.affect(target, cls);
                                    }
                                    else {
                                        if(buff instanceof FlavourBuff) {
                                            try {
                                                added = Buff.affect(target,cls,Float.parseFloat(input[index]));
                                                index++;
                                            } catch (NumberFormatException e) {
                                                added = Buff.affect(target,cls);
                                            }
                                        } else {
                                            added = Buff.affect(target, cls);
                                            // check some common methods for active buffs
                                            String[] methodNames = {"set", "reset", "prolong", "extend"};
                                            for(String methodName : methodNames) {
                                                if(success = executeMethod(methodName,added, copyOfRange(input,index,input.length)))
                                                    break;
                                            }
                                        }
                                        // attempt to call a specified method.
                                        if(!success &&
                                                index < input.length
                                                && !executeMethod(input[index], added, copyOfRange(input,index+1,input.length))
                                        ) GLog.w("Warning: No supported method matching "+input[index]+" was found.");
                                    }
                                    if(added == null) {
                                        added = Buff.affect(target, cls);
                                    }
                                    // manual announce.
                                    if(added.icon() == BuffIndicator.NONE && !added.announced) {
                                        int color; switch(added.type) {
                                            case POSITIVE:
                                                color = CharSprite.POSITIVE;
                                                break;
                                            case NEGATIVE:
                                                color = CharSprite.NEGATIVE;
                                                break;
                                            default:
                                                color = CharSprite.NEUTRAL;
                                        }
                                        target.sprite.showStatus(color, added.toString());
                                    }
                                }
                            });
                            break;
                        case SEED:
                            int a = 1;
                            if(input.length > 2) try {
                                a = Integer.parseInt(input[2]);
                            } catch (Exception e) {/*do nothing*/}
                            final int amount = a;
                            GameScene.selectCell(new CellSelector.Listener() {
                                @Override public String prompt() {
                                    return "Select the tile to seed the blob:";
                                }
                                @Override public void onSelect(Integer cell) {
                                    if(cell == null) return;
                                    GameScene.add(Blob.seed(cell, amount, (Class<Blob>)cls));
                                }
                            });
                    } else GLog.w( "%s \"%s\" not found.", command.paramClass.getSimpleName(), input[1]);
                } else onSelect(true, "help " + text); // lazy me, I know.
            }
        });
    }

    @Override public String name() {
        return "Scroll of Debug";
    }
    @Override public String desc() {
        StringBuilder builder = new StringBuilder();
        builder.append("A scroll that gives you great power, letting you create virtually any item or mob in the game.")
                .append("\n\nCommands:");
        for(Command cmd : Command.values()) builder.append("\n\n_-_ ").append(cmd.documentation());
        return builder.append("\n"
                + "\nPlease note that some possible inputs may crash the game or cause other unexpected behavior, especially if they weren't intended to be created spontaneously.")
                .toString();
    }
    @Override public boolean isIdentified() {
        return true;
    }
    @Override public boolean isKnown() { return true; }

    // fixme there's no way to know how many arguments were actually used, which forces this to be the last command.
    /** dynamic method execution logic **/
    boolean executeMethod(String methodName, Object obj, String... args) {
        ArrayList<Method> methods = new ArrayList<>();
        for(Method method : obj.getClass().getMethods()) {
            if(method.getName().equalsIgnoreCase(methodName)) methods.add(method);
        }
        Collections.sort(methods, (m1, m2) -> m2.getParameterTypes().length - m1.getParameterTypes().length );
        for(Method method : methods) try {
            method.invoke(obj, getArguments(method.getParameterTypes(), args));
            return true;
        } catch (Exception e) {/*do nothing */}
        return false;
    }

    // throws an exception if it fails. This removes the need for me to handle errors at all.
    Object[] getArguments(Class[] params, String[] input) throws Exception {
        Object[] args = new Object[params.length];
        int j = 0;
        // currently not implemented.
        IntMap<Class> delayedChecks = new IntMap<>();
        for(int i=0; i < params.length; i++) {
            Class type = params[i];
            if (type == int.class || type == Integer.class) {
                // could be a cell.
                if(input[j].equalsIgnoreCase("cell")) delayedChecks.put(i,Integer.class);
                else args[i] = Integer.parseInt(input[j]);
                j++;
            }
            else if (type == float.class || type == Float.class)
                args[i] = Float.parseFloat(input[j++]);
            else if (type == String.class)
                args[i] = input[j++];
            else if (type == Boolean.class || type == boolean.class)
                args[i] = Boolean.getBoolean(input[j++]);
            else if (Enum.class.isAssignableFrom(type)) {
                for (String name : new String[]{
                        input[j], input[j].toUpperCase(), input[j].toLowerCase()
                })
                    try {
                        args[i] = Enum.valueOf(type, name);
                    } catch (IllegalArgumentException e) {/*continue*/}
                j++;
            }
            else if(Char.class.isAssignableFrom(type)) {
                // two subclasses, which means two possible ways for this to go.
                if(type.isAssignableFrom(Hero.class) && input[j].equalsIgnoreCase("hero")) {
                    params[i] = Hero.class;
                    j++;
                }
                if(type == Hero.class) args[i] = curUser; // autofill
                delayedChecks.put(i, type);
            } else args[i] = Class.class.isAssignableFrom(type)
                    ? trie.findClass(input[j++], Object.class)
                    : Reflection.newInstanceUnhandled(trie.findClass(input[j++], type));
            if (args[i] == null && !delayedChecks.containsKey(i)) throw new IllegalArgumentException();
        }
        if(delayedChecks.notEmpty()) throw new IllegalArgumentException(); // currently not supported. logic would go here though.
        return args;
    }

    TreeMap<Class,Set<Method>> hierarchy(Class base) {
        TreeMap<Class, Set<Method>> map = new TreeMap<>((c1, c2) -> {
            int res = 0;
            if (c1.isAssignableFrom(c2)) res++;
            if (c2.isAssignableFrom(c1)) res--;
            return res;
        });
        for (Method m : base.getMethods()) {
            Class key = m.getDeclaringClass();
            Set<Method> value = map.get(key);
            if(value == null) map.put(key, value = new HashSet<>());
            value.add(m);
        }
        return map;
    }

    // ensures name uniqueness for help display. treemap so things are sorted.
    private static class ClassNameMap extends HashMap<String, Class> {
        ArrayList<String> getNames() {
            ArrayList<String> names = new ArrayList<>();
            for(Map.Entry<String,Class> entry : entrySet()) {
                if(entry.getValue() != null) names.add(entry.getKey());
            }
            Collections.sort(names);
            return names;
        }

        @Override public Class put(String key, Class cls) {
            String newKey = key;
            if(containsKey(key)) {
                // null means it's been moved.
                Class existing = get(key);
                if(existing != null) { // if it hasn't already been moved.
                    // assumes we can't create conflicts this way.
                    super.put(key, null);
                    put(extendPath(key,existing), existing);
                }
                newKey = extendPath(key, cls);
            }
            //noinspection StringEquality
            return key == newKey ? super.put(key, cls) : put(newKey, cls);
        }

        private static String extendPath(String name, Class cls) {
            if(cls == null) return name;

            String fullName = cls.getName();

            int right = fullName.indexOf(name);
            if(right == 0) return name;

            int left = fullName.lastIndexOf('$', right-2);
            if(left == -1) left = fullName.lastIndexOf('.', right-2);

            return fullName.substring(left+1, right) + name;
        }
    }

    // reflection logic.

    public static ClassLoader loader = ScrollOfDebug.class.getClassLoader();
    public static PackageTrie trie = null; // loaded when needed.
    static {
        try {
            trie = PackageTrie.getClassesForPackage(ROOT);
        } catch (ClassNotFoundException e) { ShatteredPixelDungeon.reportException(e); }
    }

    static String listAllClasses(PackageTrie trie, Class<?> parent) {
        ClassNameMap names = new ClassNameMap();
        for(Class cls : trie.getAllClasses()) {
            if(parent.isAssignableFrom(cls)) names.put(cls.getSimpleName(), cls);
        }
        StringBuilder result = new StringBuilder();
        if(!names.isEmpty()) {
            for(String name : names.getNames()) if(canInstantiate(names.get(name))) result.append("\n_-_ ").append(name);
        }
        return result.toString();
    }


    // including RKPD2 scrolling window code.
    private static class HelpWindow extends Window {
        private static final int WIDTH_MIN=120, WIDTH_MAX=220;
        HelpWindow(String message) {
            int width = WIDTH_MIN;

            RenderedTextBlock text = PixelScene.renderTextBlock(6);
            text.text(message, width);
            //text.setPos(titlebar.left(), titlebar.bottom() + 2 * GAP);

            while (PixelScene.landscape()
                    && text.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
                    && width < WIDTH_MAX) {
                text.maxWidth(width += 20);
            }

            //Component comp = new Component();
            //comp.add(text);
            //text.setPos(0, GAP);
            //comp.setSize(text.width(), text.height() + GAP * 2);
            //resize(width, (int) Math.min((int) comp.bottom() + 2 + titlebar.height() + GAP, maxHeight()));*/

            int height = (int)text.bottom();
            int maxHeight = (int)(PixelScene.uiCamera.height * 0.9);
            boolean needScrollPane = height > maxHeight;
            if(needScrollPane) height = maxHeight;
            resize((int)text.width(), height);
            if(needScrollPane) {
                Component wrapper = new Component();
                wrapper.setSize(text.width(), text.height());
                ScrollPane sp = new ScrollPane(wrapper);
                add(sp);
                wrapper.add(text);
                text.setPos(0,0);
                sp.setSize(wrapper.width(), height);
            }
            else {
                add(text);
            }
        }
    }

    /** this checks if we can create this class using Reflection. **/
    public static boolean canInstantiate(Class c) {
        // check if there's a valid constructor
        try { c.getConstructor(); } catch (NoSuchMethodException e) { return false; }
        return !( Modifier.isAbstract(c.getModifiers()) || Reflection.isMemberClass(c) && !Reflection.isStatic(c) );
    }
}