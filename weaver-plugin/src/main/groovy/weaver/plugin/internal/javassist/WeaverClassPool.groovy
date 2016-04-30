package weaver.plugin.internal.javassist

import javassist.ClassPool

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverClassPool extends ClassPool{

    WeaverClassPool() {
    }

    WeaverClassPool(boolean useDefaultPath) {
        super(useDefaultPath)
    }

    WeaverClassPool(ClassPool parent) {
        super(parent)
    }

    @Override
    ClassLoader getClassLoader() {
        return super.getClassLoader()
    }
}
