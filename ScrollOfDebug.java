package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
// Commands
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
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

import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import sun.net.www.protocol.file.FileURLConnection;

/**
 * Scroll of Debug uses ClassLoader to get every class that can be directly created and provides a command interface with which to interact with them.
 *
 * See Command for a description of all commands.
 *
 * @implNote The storage mechanism for the classes is currently...not needed and I may simplify it down to a standard list.
 *
 * @author Zrp200
 * @version v0.1.0
 * **/
@SuppressWarnings("rawtypes")
public class ScrollOfDebug extends Scroll {
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
    }

    private enum Command {
        HELP(null, // ...
                "[COMMAND | all]",
                "Gives more information on commands, or in the case of 'all', all of them."),
        GIVE(Item.class,
                "ITEM [_+_LEVEL] [_x_QUANTITY]",
                "Creates and puts into your inventory the generated item."),
        SPAWN(Mob.class,
                "MOB",
                "Summons the indicated mob and randomly places them on the depth."),
        AFFECT(Buff.class,
                "BUFF [duration]",
                "Allows you to attach a buff to a character in sight. This can be extremely dangerous, or it could do literally nothing.");

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
                documentation += "\n\n_Valid Classes_:" + trie.listAllClasses(paramClass);
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

                if (trie == null) try {
                    trie = getClassesForPackage("com");
                } catch (ClassNotFoundException e) { ShatteredPixelDungeon.reportException(e); }

                String[] input = text.split(" ");

                Command command; try { command = Command.valueOf(input[0].toUpperCase()); }
                catch (Exception e) {
                    GLog.w("\""+input[0]+"\" is not a valid command.");
                    return;
                }

                if(command == Command.HELP) {
                    String output = null;
                    boolean all = false;
                    if(input.length > 1) {
                        // we only care about the initial argument.
                        Command cmd = Command.get(input[1]);
                        if(cmd != null) output = cmd.fullDocumentation(trie);
                        else all = input[1].equalsIgnoreCase("all");
                    }
                    if(output == null) {
                        StringBuilder builder = new StringBuilder();
                        for(Command cmd : Command.values()) {
                            if(all) {
                                // extensive. help is omitted because we are using help.
                                if(cmd != Command.HELP) {
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
                    final Class cls = trie.findClass(input[1]);
                    boolean valid = true;
                    Object o = null; try {
                        o = Reflection.newInstanceUnhandled(cls);
                    } catch (Exception e) { valid = false; }
                    if (valid && command.paramClass.isInstance(o)) switch (command) {
                        case SPAWN: Mob mob = (Mob)o;
                            mob.pos = Dungeon.level.randomRespawnCell(mob);
                            if(mob.pos != 1) {
                                GameScene.add(mob);
                                GLog.w("Summoned " + mob.name());
                            }
                            break;
                        case GIVE: Item item = (Item)o;
                            item.identify();
                            // todo add enchants/glyphs for weapons/armor?
                            // process modifiers left to right (so later ones have higher precedence)
                            for(int i=2; i < input.length; i++) try {
                                int number = Integer.parseInt(input[i].substring(1));
                                switch(input[i].toLowerCase(Locale.ENGLISH).charAt(0)) {
                                    case 'x':
                                        item.quantity(number);
                                        break;
                                    case '+':
                                        item.level(number);
                                        break;
                                }
                            } catch (NumberFormatException e) {/* do nothing */}
                            if (item.collect()) {
                                boolean important = item.unique && (item instanceof Scroll || item instanceof Potion);
                                String pickupMessage = Messages.get(curUser, "you_now_have", item);
                                if(important) GLog.p(pickupMessage); else GLog.i(pickupMessage);
                                Sample.INSTANCE.play(Assets.Sounds.ITEM);
                                GameScene.pickUp(item, curUser.pos);
                            }
                            break;
                        case AFFECT:
                            Buff buff = (Buff)o;

                            Float d = null;
                            if(input.length > 2) try {
                                d = Float.parseFloat(input[2]);
                            } catch (NumberFormatException e) {/*do nothing*/}
                            final Float duration = d;

                            GameScene.selectCell(new CellSelector.Listener() {
                                @Override public String prompt() {
                                    return "Select the character to apply the buff to:";
                                }
                                @Override public void onSelect(Integer cell) {
                                    Char target;
                                    if(cell == null || cell == -1 || (target = Actor.findChar(cell)) == null) return;
                                    Buff added = null;
                                    if(duration != null) {
                                        // this is the fun part.
                                        if(buff instanceof FlavourBuff) added = Buff.affect(target, cls, duration);
                                        else {
                                            String[] methodNames = input.length > 3 ? new String[]{input[3]} :
                                                    new String[]{"set","prolong","extend"};
                                            Class[] paramTypes = {float.class, int.class};
                                            for(String methodName : methodNames) for(Class paramType : paramTypes) try {
                                                cls.getMethod(methodName, paramType)
                                                        .invoke(added, paramType == float.class ? duration : duration.intValue());
                                                break;
                                            } catch (NoSuchMethodException e) {/*continue*/}
                                            catch (Exception e) { e.printStackTrace(); }
                                        }
                                    }
                                    if(added == null) {
                                        added = Buff.affect(target, cls);
                                    }
                                    // manual announce.
                                    if(added.icon() == BuffIndicator.NONE && !added.announced) {
                                        int color; switch(added.type) {
                                            case POSITIVE:
                                                color = CharSprite.POSITIVE;
                                            case NEGATIVE:
                                                color = CharSprite.NEGATIVE;
                                            default:
                                                color = CharSprite.NEUTRAL;
                                        }
                                        target.sprite.showStatus(color, added.toString());
                                    }
                                }
                            });
                            break;
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
                + "\nPlease note that some possible inputs may crash the game or cause other unexpected behavior, especially if they weren't intended to be created spontaneously. "
                + "\nThe scroll also currently does not function on mobile devices.")
                .toString();
    }
    @Override public boolean isIdentified() {
        return true;
    }
    @Override public boolean isKnown() { return true; }

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

    // this structure is *probably* not needed, but if you wanted to be able to filter easily by directory this makes it easy.
    // and I kiinda wanted to do this.
    static class PackageTrie {
        private final HashMap<String, PackageTrie> subTries = new HashMap<>();
        private final ArrayList<Class<?>> classes = new ArrayList<>();

        // this is used for displaying valid arguments for commands, currently.
        String listAllClasses(Class<?> parent) {
            ClassNameMap names = new ClassNameMap();
            for(Class cls : getAllClasses()) {
                if(parent.isAssignableFrom(cls)) names.put(cls.getSimpleName(), cls);
            }
            StringBuilder result = new StringBuilder();
            if(!names.isEmpty()) {
                for(String name : names.getNames()) result.append("\n_-_ ").append(name);
            }
            return result.toString();
        }

        private void add(String pkg, PackageTrie tree) {
            if(!tree.isEmpty()) subTries.put(pkg, tree);
        }

        /** finds a package somewhere in the trie.
         *
         * fixme/todo This is not used for #findClass because it stops at first match. If it returned a list it would work, probably.
         * fixme this (and #findClass) do not handle duplicated results well at all. This isn't an issue for me, but it COULD be an issue.
         **/
        public PackageTrie findPackage(String name) {
            return findPackage(name.split("\\."), 0);
        }
        public PackageTrie findPackage(String[] path, int index) {
            if(index == path.length) return this;

            PackageTrie
                    match = getPackage(path[index]),
                    found = match != null ? match.findPackage(path, index+1) : null;

            if(found != null) return found;
            for(PackageTrie trie : subTries.values()) {
                if(trie == match) continue;
                found = trie.findPackage(path, 0);
                if(found != null) return found;
            }
            return null;
        }

        private Class<?> findClass(String name) {
            return findClass(name.split("\\."), 0);
        }
        private Class<?> findClass(String[] path, int i) {
            if(i == path.length) return null;

            Class<?> found = null;
            PackageTrie match = null;
            if(i+1 < path.length) {
                match = getPackage(path[i]);
                if (match != null) {
                    found = match.findClass(path, i + 1);
                    if (found != null) return found;
                }
            } else if( ( found = getClass(path[i]) ) != null) return found;
            ArrayList<PackageTrie> toSearch = new ArrayList(subTries.values());
            toSearch.remove(match);
            for(PackageTrie tree : toSearch) if( (found = tree.findClass(path,i)) != null ) break;
            return found;
        }

        // does not deep search
        public PackageTrie getPackage(String packageName) {
            return subTries.get(packageName);
        }
        // this is probably not efficient or even taking advantage of what I've done.
        public Class<?> getClass(String className) {
            boolean hasQualifiers = className.matches("[.$]");
            for(Class<?> cls : classes) {
                boolean match = hasQualifiers
                        ? cls.getName().toLowerCase(Locale.ROOT).endsWith( className.toLowerCase(Locale.ROOT) )
                        : cls.getSimpleName().equalsIgnoreCase(className);
                if(match) return cls;
            }
            return null;
        }

        public ArrayList<Class> getAllClasses() {
            ArrayList<Class> classes = new ArrayList(this.classes);
            for(PackageTrie tree : subTries.values()) classes.addAll(tree.getAllClasses());
            return classes;
        }

        public boolean isEmpty() { return subTries.isEmpty() && classes.isEmpty(); }

        private PackageTrie getOrCreate(String pkg) {
            PackageTrie stored = subTries.get(pkg);
            if(stored == null) subTries.put(pkg, stored = new PackageTrie());
            return stored;
        }
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

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @link https://stackoverflow.com/a/22462785/4258976
     *
     * @implNote I modified it to work with a trie, but that implementation will still get all classes.
     *
     * @param pckgname
     *            the package name to search
     * @return a trie of classes found in that package.
     * @throws ClassNotFoundException
     *             if something went wrong
     */
    public static PackageTrie getClassesForPackage(String pckgname)
            throws ClassNotFoundException {
        PackageTrie root = new PackageTrie(); // root.

        try {
            if (loader == null) throw new ClassNotFoundException("Can't get class loader.");

            final Enumeration<URL> resources = loader.getResources(pckgname.replace('.', '/'));
            URLConnection connection;

            while(resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if(url == null) break;
                try {
                    connection = url.openConnection();

                    if (connection instanceof JarURLConnection) {
                        checkJarFile((JarURLConnection) connection, pckgname, root);
                    } else if (connection instanceof FileURLConnection) {
                        try {
                            checkDirectory(
                                    new File(URLDecoder.decode(url.getPath(),
                                            "UTF-8")), pckgname, root);
                        } catch (final UnsupportedEncodingException ex) {
                            throw new ClassNotFoundException(
                                    pckgname + " does not appear to be a valid package (Unsupported encoding)",
                                    ex);
                        }
                    } else
                        throw new ClassNotFoundException(
                                pckgname +" ("+ url.getPath() +") does not appear to be a valid package");
                } catch (final IOException ioex) {
                    throw new ClassNotFoundException(
                            "IOException was thrown when trying to get all resources for "
                                    + pckgname, ioex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new ClassNotFoundException(
                    pckgname+" does not appear to be a valid package (Null pointer exception)",
                    ex);
        } catch (final IOException ioex) {
            throw new ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for "
                            + pckgname, ioex);
        }
        return root;
    }

    private static PackageTrie checkDirectory(File directory, String pckgname, PackageTrie trie) throws ClassNotFoundException {
        File tmpDirectory;

        if (directory.exists() && directory.isDirectory()) {
            final String[] files = directory.list();

            for (final String file : files) {
                if (file.endsWith(".class")) {
                    try {
                        Class cls = Class.forName(pckgname + '.'
                                + file.substring(0, file.length() - 6));
                        if(canInstantiate(cls)) trie.classes.add(cls);
                    } catch (final NoClassDefFoundError e) {
                        // do nothing. this class hasn't been found by the
                        // loader, and we don't care.
                    }
                } else if ( (tmpDirectory = new File(directory, file) ).isDirectory()) {
                    trie.add(file, checkDirectory(tmpDirectory, pckgname + "." + file, new PackageTrie()));
                }
            }
        }
        return trie;
    }

    private static void checkJarFile(JarURLConnection connection,
                                     String pckgname,
                                     PackageTrie tree)
            throws ClassNotFoundException, IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();

        while(entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if(jarEntry == null) break;

            String name = jarEntry.getName();
            int index = name.indexOf(".class");
            if(index == -1) continue;

            name = name.substring(0, index)
                    .replace('/', '.');

            Class<?> cls;
            if (name.contains(pckgname) && canInstantiate(cls = Class.forName(name))) {
                PackageTrie cur = tree;
                for(String s : cls
                        .getPackage()
                        .getName()
                        .substring( pckgname.length() )
                        .split("\\.")) {
                    if(!s.isEmpty()) cur = cur.getOrCreate(s);
                }

                cur.classes.add(cls);
            }
        }
    }

}