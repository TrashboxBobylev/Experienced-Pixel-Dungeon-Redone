package com.zrp200.scrollofdebug;

import static java.util.Collections.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import sun.net.www.protocol.file.FileURLConnection;

public class PackageTrie {
    private final HashMap<String, PackageTrie> subTries = new HashMap<>();
    private final ArrayList<Class<?>> classes = new ArrayList<>();

    public final Map<String,PackageTrie> getSubtries()  { return unmodifiableMap(subTries); }
    public final List<Class<?>> getClasses()            { return unmodifiableList(classes); }

    protected void add(String pkg, PackageTrie tree) {
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

    public Class<?> findClass(String name, Class parent) {
        return findClass(name.split("\\."), parent, 0);
    }
    // known issues: duplicated classes may mask each other.
    public Class<?> findClass(String[] path, Class parent, int i) {
        if(i == path.length) return null;

        Class<?> found = null;
        PackageTrie match = null;
        if(i+1 < path.length) {
            match = getPackage(path[i]);
            if (match != null) {
                found = match.findClass(path, parent, i + 1);
                if (found != null && (parent == null || parent.isAssignableFrom(found)) ) return found;
            }
        } else if( ( found = getClass(path[i]) ) != null && (parent == null || parent.isAssignableFrom(found))) return found;
        else found = null;
        ArrayList<PackageTrie> toSearch = new ArrayList(subTries.values());
        toSearch.remove(match);
        for(PackageTrie tree : toSearch) if( (found = tree.findClass(path,parent,i)) != null ) break;
        return found;
    }

    // does not deep search
    public PackageTrie getPackage(String packageName) {
        return subTries.get(packageName);
    }
    // this is probably not efficient or even taking advantage of what I've done.
    public Class<?> getClass(String className) {
        boolean hasQualifiers = className.contains("$") || className.contains(".");
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

    protected final PackageTrie getOrCreate(String pkg) {
        if(pkg == null || pkg.isEmpty()) return this;
        String[] split = pkg.split("\\.", 2);
        // [0] is stored, [1] is recursively added.
        PackageTrie stored = subTries.get(split[0]);
        if(stored == null) subTries.put(split[0], stored = new PackageTrie());
        return split.length == 1 ? stored : stored.getOrCreate(split[1]);
    }
    protected final void addClass(Class cls, String pkg) {
        String clsPkg = cls.getPackage().getName();
        if(clsPkg.equals(pkg)) classes.add(cls);
        else if(clsPkg.startsWith(pkg)) getOrCreate(clsPkg.substring(pkg.length()+1)).classes.add(cls);
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
        PackageTrie root = new PackageTrie();
        ClassLoader loader = PackageTrie.class.getClassLoader();

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
                        //if(canInstantiate(cls))
                        trie.classes.add(cls);
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
            if (name.contains(pckgname) /*&& canInstantiate(cls = Class.forName(name))*/) {
                tree.addClass(Class.forName(name),pckgname);
            }
        }
    }
}
