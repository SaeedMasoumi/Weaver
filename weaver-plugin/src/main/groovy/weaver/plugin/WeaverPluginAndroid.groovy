package weaver.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

import static weaver.plugin.task.TaskManager.createAndroidTransformerTask

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverPluginAndroid implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.afterEvaluate {
            def isLibrary = project.plugins.hasPlugin(LibraryPlugin)
            if (isLibrary) {
                def android = project.extensions.getByType(LibraryExtension)
                android.libraryVariants.all { BaseVariant variant ->
                    createAndroidTransformerTask project, variant
                }
            } else {
                def android = project.extensions.getByType(AppExtension)
                android.applicationVariants.all { BaseVariant variant ->
                    def transformerTask = createAndroidTransformerTask project, variant
                    variant.install?.dependsOn(transformerTask)
                }
            }
        }
    }

}
