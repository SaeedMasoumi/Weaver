package weaver.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverPlugin implements Plugin<Project> {

    static final String WEAVER_CONF_NAME = "weaver"

    @Override
    void apply(Project project) {
        //Add weaver extension
        project.extensions.create('weaver', WeaverExtension)

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
            addConfigurationForAndroid project
            project.apply plugin: WeaverPluginAndroid
        }
    }

    static void createWeaverConfiguration(Project project) {
        Configuration compileConf = project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
        def weaverConf = project.configurations.create("weaver")
                .setVisible(false)
                .setDescription("Like $compileConf.name, but it will not add any scopes to generated pom file")
        compileConf.extendsFrom(weaverConf)
    }

    static void addConfigurationForAndroid(Project project) {
        project.configurations.create(WEAVER_CONF_NAME)
                .extendsFrom(project.configurations.compile, project.configurations.provided)
    }

}
