package weaver.plugin.internal.javassist

import groovy.transform.CompileStatic
import javassist.ClassPath
import javassist.ClassPool
import javassist.CtClass
import javassist.Loader
import org.gradle.api.file.FileCollection

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@CompileStatic
class WeaverClassPool extends ClassPool {

    private ClassLoader parentClassLoader
    private Loader javassistLoader
    private List<ClassPath> classPaths = new ArrayList<>()

    WeaverClassPool(ClassLoader parentClassLoader, boolean useDefaultPath) {
        super(useDefaultPath)
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
        classPaths.add(appendClassPath(file.path))
    }

    void close() {
        classPaths.each { it.close() }
    }

}
