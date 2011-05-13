package cua.li.ti.whereis;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>So you have lost your class? Find it back with:
 * <code>java -jar whereis.jar MyClass /in/MyApps</code>
 * <br /> It is that simple.</p>
 * <p>When working with J2EE applications, you may encounter two main painful
 * situations: when you are getting a <code>ClassNotFoundException</code> or when
 * it is giving you a <code>ClassCastException</code>. As you have several web
 * applications packed into your main enterprise application and each of these web
 * applications has several libraries packing many classes, you end up losing a
 * bit of control about the key issue: <q><em>Where is the class?</em></q></p>
 * <p>In the first case, you have to check in your development workspace where the
 * class is packed in. This way you may identify some missing library in the
 * erroneous deployment.</p>
 * <p>In the second case, you may identify the various places the class is defined
 * in. Usually one these is superfluous and should get out of the way for the
 * application to work correctly.</p>
 * <p>This tool is to be run as follows:</p>
 * <p><code>java -jar <a href="http://cua.li/TI/whereis.jar">whereis.jar</a> QName
 * /opt/jboss</code></p>
 * <p>As this tool is making use of the temporary directory to expand the embedded
 * archives, you may have to change the <code>java.io.tmpdir</code> property to a
 * directory you have write permissions to.</p>
 * <p><code>java -Djava.io.tmpdir=D:\tmp -jar <a href="http://cua.li/TI/whereis.jar">whereis.jar</a>
 * SAXParserFactory D:\JBoss</code></p>
 * <p>The given directory is searched recursively as well as every archive with
 * extension <code>.car</code>, <code>.ear</code>, <code>.jar</code>,
 * <code>.rar</code>, <code>.sar</code> or <code>.war</code> and their embedded
 * archives. The search directory may be omitted. It would then default to the
 * current working directory.</p>
 *
 * @author A@cua.li
 */
public final class WhereIs {
    private static final String JAR_ARCHIVE_PATTERN = "^.*\\.[cejrsw]ar$";
    private static final FileFilter DIR_FILTER = new FileFilter() {
        public boolean accept(final File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }
            if (pathname.getAbsolutePath().matches(JAR_ARCHIVE_PATTERN)) {
                return true;
            }
            return false;
        }
    };
    private static final String STARTING_ANYTHING_PATTERN = "^.*";
    private static final String ENDING_CLASS_PATTERN = "\\.class$";
    private static final String CLASS_ARCHIVE_PATTERN = STARTING_ANYTHING_PATTERN + ENDING_CLASS_PATTERN;
    private static final FileFilter CLASS_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return ((!pathname.isDirectory()) && pathname.getAbsolutePath().matches(CLASS_ARCHIVE_PATTERN));
        }
    };

    public static void main(final String[] args) {
        if (1 > args.length) {
            System.out.println("usage: WhereIs SomeClass [directory]");
            System.exit(1);
        }
        String dirName = ".";
        if (1 < args.length) {
            dirName = args[1];
        }
        final String className = args[0];
        final File directory = new File(dirName);
        WhereIs.locate(className, directory);
    }

    private static void locate(String className, File directory) {
        final StringBuilder buffer = new StringBuilder(300);
        final File[] classes = directory.listFiles(WhereIs.CLASS_FILTER);
        for (File classe : classes) {
            buffer.setLength(0);
            buffer.append(STARTING_ANYTHING_PATTERN).append(className).append(ENDING_CLASS_PATTERN);
            if (classe.getName().matches(buffer.toString())) {
                System.out.println(directory.getAbsolutePath() + "::" + classe.getName());
            }
        }
        final File[] files = directory.listFiles(WhereIs.DIR_FILTER);
        for (File file : files) {
            if (file.isDirectory()) {
                WhereIs.locate(className, file);
            } else {
                try {
                    buffer.setLength(0);
                    buffer.append(directory.getAbsolutePath()).append("::").append(file.getName());
                    WhereIs.locate(className, new JarFile(file), buffer.toString());
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private static void locate(final String className, final JarFile jar, final String path) {
        final List<JarEntry> entries = Collections.list(jar.entries());
        for (JarEntry entry : entries) {
            WhereIs.locate(className, jar, entry, path);
        }
    }

    private static void locate(final String className, final JarFile jar, final JarEntry entry, final String path) {
        if (entry.getName().matches(STARTING_ANYTHING_PATTERN + className + ENDING_CLASS_PATTERN)) {
            System.out.println(path + "::" + entry.getName());
        } else if (entry.getName().matches(JAR_ARCHIVE_PATTERN)) {
            try {
                final File tmpJar = WhereIs.extractEmbeddedJar(jar.getInputStream(entry));
                WhereIs.locate(className, new JarFile(tmpJar), path + "::" + entry.getName());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static File extractEmbeddedJar(InputStream is) throws FileNotFoundException, IOException {
        final File tmpJar = File.createTempFile("whereis", ".jar");
        tmpJar.deleteOnExit();
        final FileOutputStream fos = new FileOutputStream(tmpJar);
        final byte[] buffer = new byte[1024];
        try { 
            int read = is.read(buffer);
            while (-1 < read) {
                fos.write(buffer, 0, read);
                read = is.read(buffer);
            }
        } finally {
            is.close();
            fos.close();
        }
        return tmpJar;
    }
}
