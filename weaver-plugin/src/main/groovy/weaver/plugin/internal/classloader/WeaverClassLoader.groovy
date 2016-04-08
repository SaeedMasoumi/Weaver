package weaver.plugin.internal.classloader
/**
 * Includes
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@Singleton
class WeaverClassLoader {

    private ClassLoader classLoader;
    private ArrayList<String> processorsClassNameس

    public void loadJars(Set<File> jarFiles) {
        if (jarFiles) {
            def urls = jarFiles.collect() { it.toURI().toURL() }
            classLoader = new URLClassLoader(urls as URL[])
        } else {
            classLoader = new URLClassLoader()
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader
    }

    public void setWeaverProcessors(ArrayList<String> classNames) {
        processorsClassNameس = classNames
    }

}
