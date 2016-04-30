package weaver.plugin.internal.util

import org.gradle.api.Project
import weaver.plugin.WeaverPlugin

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverConfigurationScope {

    /**
     *
     * @return Returns dependencies contained in weaver configuration and ignoring super configurations(compile and provided).
     */
    static Set<File> getDependencies(Project project) {
        return getWeaverConfiguration(project).files
    }

    /**
     * @return Returns All jar files from 'weaver' scope dependencies. For '.aar' bundles, which are the binary distribution of Android Libraries,
     * it will include only <code>/classes.jar</cod> and <code>/libs/*.jar</code> (if exist).
     * <p>
     * See <a href="http://tools.android.com/tech-docs/new-build-system/aar-format">AAR Format</a> for more details.
     */
    static Set<File> getJarFiles(Project project) {
        Set<File> dependencies = getDependencies project
        def jarFiles = []
        for (File it : dependencies) {
            if (it.name.endsWith(".jar")) {
                jarFiles.add(it)
            } else if (it.name.endsWith(".aar")) {
                jarFiles.addAll(
                        project.zipTree(it).matching {
                            include '**/*.jar'
                            exclude 'lint.jar'
                        }.files
                )
            }
        }
        return jarFiles
    }

    static def getWeaverConfiguration(Project project) {
        return project.configurations.getByName(WeaverPlugin.WEAVER_CONF_NAME)
    }

}
