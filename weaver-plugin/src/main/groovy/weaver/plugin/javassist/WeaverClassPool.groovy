package weaver.plugin.javassist

import javassist.*
import org.gradle.api.file.FileCollection
import weaver.plugin.util.Disposable

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverClassPool extends ClassPool implements Disposable {

    ClassLoader parentClassLoader
    Loader javassistLoader
    List<ClassPath> classPaths

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
        if (!classPaths) classPaths = new ArrayList<>()
        classPaths.add(classpath)
        return classpath
    }

    @Override
    void dispose() {
        if (classPaths)
            classPaths.each { it.close() }
    }
}
