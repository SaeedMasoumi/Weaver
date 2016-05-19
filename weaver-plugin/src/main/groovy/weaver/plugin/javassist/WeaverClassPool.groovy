package weaver.plugin.javassist

import groovy.transform.CompileStatic
import javassist.*
import org.gradle.api.file.FileCollection
import weaver.plugin.util.Disposable

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@CompileStatic
class WeaverClassPool extends ClassPool implements Disposable {

    private ClassLoader parentClassLoader
    private Loader javassistLoader
    private List<ClassPath> classPaths = new ArrayList<>()

    WeaverClassPool(ClassLoader parentClassLoader) {
        super(true)
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

    ClassPath appendClassPath(File file) {
        return appendClassPath(file.absolutePath)
    }

    @Override
    ClassPath appendClassPath(ClassPath cp) {
        return storeClassPath(super.appendClassPath(cp))
    }

    @Override
    ClassPath appendClassPath(String pathname) throws NotFoundException {
        return storeClassPath(super.appendClassPath(pathname))
    }

    @Override
    ClassPath appendSystemPath() {
        return storeClassPath(super.appendSystemPath())
    }

    ClassPath storeClassPath(ClassPath classpath) {
        classPaths.add(classpath)
        return classpath
    }

    @Override
    void dispose() {
        classPaths.each { it.close() }
    }
}
