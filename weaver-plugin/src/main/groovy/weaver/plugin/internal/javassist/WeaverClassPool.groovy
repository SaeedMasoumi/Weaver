package weaver.plugin.internal.javassist

import javassist.ClassPool
import javassist.CtClass
import javassist.Loader
import net.openhft.compiler.CachedCompiler
import net.openhft.compiler.CompilerUtils
import org.gradle.api.file.FileCollection

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverClassPool extends ClassPool {

    private ClassLoader parentClassLoader
    private Loader javassistLoader
    private CachedCompiler cachedCompiler = CompilerUtils.CACHED_COMPILER

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

    void setCachedCompiler(File generatedClassDir) {
        cachedCompiler = new CachedCompiler(null, generatedClassDir)
        appendClassPath(generatedClassDir)
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

    CtClass getFromJavaCode(String className, String javaCode) {
        return get(cachedCompiler
                .loadFromJava(parentClassLoader, className, javaCode)
                .getCanonicalName())
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
        CompilerUtils.addClassPath(file.path)
    }

}
