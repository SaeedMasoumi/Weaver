package weaver.plugin.classloader

import weaver.processor.WeaverProcessor

/**
 * Includes
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@Singleton
class WeaverClassLoader {

    private ClassLoader classLoader;
    private WeaverProcessor[] processors

    public void loadJars(Set<File> jarFiles) {
        if (jarFiles) {
            def urls = jarFiles.collect() { it.toURI().toURL() }
            classLoader = new URLClassLoader(urls as URL[])
        } else {
            classLoader = new URLClassLoader()
        }
    }

    public URLClassLoader getClassLoader() {
        return classLoader
    }
}
