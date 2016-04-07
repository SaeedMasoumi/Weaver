package weaver.plugin.classloader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@Singleton
class WeaverClassLoader {

    private ClassLoader classLoader;

    public void load(Set<File> files) {
        def urls = files.collect() { it.toURI().toURL() }
        def time = System.currentTimeMillis()
        classLoader = new URLClassLoader(urls.toArray() as URL[])
        println "current " + (System.currentTimeMillis() - time)
    }

    public URLClassLoader getClassLoader() {
        return classLoader
    }
}
