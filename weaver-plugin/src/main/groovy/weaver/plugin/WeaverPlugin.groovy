package weaver.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverPlugin implements Plugin<Project> {

    static final String WEAVER_EXT_NAME = "weaver"
    static final String WEAVER_CONFIGURATION = "weaver"
    static final String TEST_WEAVER_CONFIGURATION = "testWeaver"

    @Override
    void apply(Project project) {
        //Add weaver extension
        project.extensions.create(WEAVER_EXT_NAME, WeaverExtension)
        project.configurations.create(WEAVER_CONFIGURATION)
        project.configurations.create(TEST_WEAVER_CONFIGURATION)
        //Apply weaver java plugin
        project.plugins.withType(JavaPlugin) {
            project.apply plugin: WeaverPluginJava
        }
        project.plugins.withType(ApplicationPlugin) {
            //TODO need test
        }

        //Apply weaver android plugin
        def hasPlugin = { String id -> project.plugins.hasPlugin(id) }
        if (hasPlugin("com.android.application") || hasPlugin("android") ||
                hasPlugin("com.android.library") || hasPlugin("android-library")) {
            //Add weaver configuration
            project.apply plugin: WeaverPluginAndroid
        }
    }

}
