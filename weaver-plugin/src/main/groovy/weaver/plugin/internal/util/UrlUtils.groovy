package weaver.plugin.internal.util

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class UrlUtils {

    /**
     * Normalizes directory name to ends with a '/', So {@link URLClassLoader} can accept it as a directory.
     */
    public static URL normalizeDirectoryForClassLoader(File file) {
        def externalForm = file.toURI().toURL().toExternalForm()
        if (!externalForm.endsWith('/'))
            externalForm += '/'
        return new URL(externalForm)
    }
}
