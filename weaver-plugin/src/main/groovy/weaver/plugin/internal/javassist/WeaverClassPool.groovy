package weaver.plugin.internal.javassist

import javassist.ClassPool
import javassist.CtClass
import javassist.Loader
import org.gradle.api.file.FileCollection

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverClassPool extends ClassPool {

    private ClassLoader parentClassLoader
    private Loader javassistLoader

    WeaverClassPool(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader
    }

    WeaverClassPool(ClassLoader parentClassLoader, boolean useDefaultPath) {
        super(useDefaultPath)
        this.parentClassLoader = parentClassLoader
    }

    WeaverClassPool(ClassLoader parentClassLoader, ClassPool parent) {
        super(parent)
        this.parentClassLoader = parentClassLoader
    }

    @Override
    ClassLoader getClassLoader() {
        if (javassistLoader == null)
            javassistLoader = new Loader(parentClassLoader, this)
        return javassistLoader
    }
    /**
     * Reads a class file from the given file and returns a reference
     * to the {@code CtClass}.
     */
    public CtClass get(File classFile) {
        InputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(classFile)));
        CtClass clazz = makeClass(stream);
        stream.close();
        return clazz;
    }

    void appendClassPath(Set<File> files) {
        files.each {
            appendClassPath(it)
        }
    }

    void appendClassPath(FileCollection fileCollection) {
        fileCollection.each {
            appendClassPath(it)
        }
    }

    void appendClassPath(File file) {
        appendClassPath(file.path)
    }

}
