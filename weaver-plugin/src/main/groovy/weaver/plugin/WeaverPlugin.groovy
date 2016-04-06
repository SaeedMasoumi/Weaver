package weaver.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //First find the type of current project.
        PluginType pluginType = findPluginType { String it -> project.plugins.findPlugin(it) }
        //Add weaver configuration

        //Add weaver extension
    }

    private static PluginType findPluginType(Closure hasPlugin) {
        if (hasPlugin("com.android.application") || hasPlugin("android") ||
                hasPlugin("com.android.test")) {
            return PluginType.ANDROID
        } else if (hasPlugin("com.android.library") || hasPlugin("android-library")) {
            return PluginType.ANDROID_LIB
        } else if (hasPlugin("java")) {
            return PluginType.JAVA
        } else {
            throw new ProjectConfigurationException("The android/android-library/java plugin must be applied to the project", null)
        }
    }
}
