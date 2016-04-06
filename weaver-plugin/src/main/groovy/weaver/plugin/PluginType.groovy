package weaver.plugin

/**
 * PluginType that has been applied to current module.
 * <p>
 * Currently we only support android application, android library and plain java projects.
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
enum PluginType {
    ANDROID("applicationVariants"), ANDROID_LIB("libraryVariants"), JAVA("")
    private def variants;

    PluginType(def variants) {
        this.variants = variants;
    }

    def getVariants() {
        return variants
    }
}