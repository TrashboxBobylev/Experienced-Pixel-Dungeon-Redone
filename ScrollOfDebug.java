package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

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
 * Currently the commands supported are
 * 'give' (which gives an item),
 * 'spawn' (which spawns a mob),
 * 'help' (which dumps a list of classes that can be attempted to interact with)
 *
 * @implNote The storage mechanism for the classes is currently...not needed and I may simplify it down to a standard list.
 *
 * @author Zrp200
 * @version v0.0.0
 * **/
@SuppressWarnings("rawtypes")
public class ScrollOfDebug extends Scroll {
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public String name() {
        return "Scroll of Debug";
    }

    @Override
    public String desc() { 
        return "A scroll that gives you great power, letting you create virtually any item or mob in the game" 
                + "\n\nCurrently supported commands are: " 
                + "\n_- spawn_ (spawns a mob, given as a separate argument like 'summon goo') " 
                + "_- give_ (as in 'give amulet'), which creates and puts into your inventory the generated item. It also supports quantity ('+#') and upgrades ('+#')."
                + "_- help_ (gives a list of all possible inputs that can be given to the other two commands)"
                + "\n\nPlease note that some possible inputs may crash the game or cause other unexpected behavior, especially if they weren't intended to be created spontaneously." 
                + "\n\nThe scroll currently does not function on mobile devices.";
    }

    @Override
    public void doRead() {
        GameScene.show(new WndTextInput("Enter Command:", "", 100, false,
                "Execute", "Cancel") {
            @Override
            public void onSelect(boolean positive, String text) {
                if (trie == null) try {
                    trie = getClassesForPackage("com");
                } catch (ClassNotFoundException e) { ShatteredPixelDungeon.reportException(e); }
                String[] commands = text.split(" ");

                // help command.
                // todo allow specification of package to narrow results, and perhaps a verbose argument to see whole package.
                if (commands[0].equalsIgnoreCase("help")) {
                    // this does not display properly on shpd-based mods, as only rkpd2-based mods have autoscroll.

                    String output =
                            getRefCmdClasses("give", Item.class, trie) +
                            getRefCmdClasses("spawn", Mob.class, trie);
                    GameScene.show(new WndTitledMessage(new ItemSprite(ScrollOfDebug.this), "Available Classes", output.trim()));
                }
                if (commands.length > 1) {
                    Class cls = trie.findClass(commands[1]);

                    if (commands[0].equalsIgnoreCase("spawn")) {
                        if (cls != null && Mob.class.isAssignableFrom(cls)) {
                            Mob mob = Reflection.newInstance((Class<Mob>) cls);
                            mob.pos = Dungeon.level.randomRespawnCell(mob);

                            if (mob.pos != -1) {
                                GameScene.add(mob);
                                GLog.w("Summoned " + mob.name());
                            }
                        } else {
                            GLog.w("Mob not found.");
                        }
                    } else if (commands[0].equalsIgnoreCase("give")) {
                        Item item;
                        // todo add enchants/glyphs for weapons/armor?
                        if (cls != null && Item.class.isAssignableFrom(cls)
                                && ( item = Reflection.newInstance((Class<Item>)cls) ) != null) {
                            item.identify();
                            // process modifiers left to right (so later ones have higher precedence)
                            for(int i=2; i < commands.length; i++) try {
                                int number = Integer.parseInt(commands[i].substring(1));
                                switch(commands[i].toLowerCase(Locale.ENGLISH).charAt(0)) {
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
                        }
                    }
                }
                collect();
            }
        });
    }

    // gets the corresponding help menu.
    String getRefCmdClasses(String command, Class baseClass, PackageTrie trie) {
        ClassNameMap names = new ClassNameMap();
        for(Class cls : trie.getAllClasses()) {
            if(baseClass.isAssignableFrom(cls)) names.put(cls.getSimpleName(), cls);
        }
        StringBuilder result = new StringBuilder();
        if(!names.isEmpty()) {
            result.append("\n\n_").append(command).append("_:");
            for(String name : names.getNames()) result.append("\n_-_ ").append(name);
        }
        return result.toString();
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

    // this structure is *probably* not needed, but if you wanted to be able to filter easily by directory this makes it easy.
    // and I kiinda wanted to do this.
    static class PackageTrie {
        private final HashMap<String, PackageTrie> subTries = new HashMap<>();
        private final ArrayList<Class<?>> classes = new ArrayList<>();

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
            for(Class<?> cls : classes) if(cls.getName().toLowerCase(Locale.ROOT).endsWith(className.toLowerCase(Locale.ROOT)))
                return cls;
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
