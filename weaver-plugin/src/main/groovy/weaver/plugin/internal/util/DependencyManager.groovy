package weaver.plugin.internal.util
/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class DependencyManager {

    public static ArrayList<URL> jarToURL(Set<File> files) {
        def urls = []
        files.each {
            if (it.name.endsWith(".jar")) {
                urls += it.toURI().toURL()
            }
        }
        return urls
    }
}
