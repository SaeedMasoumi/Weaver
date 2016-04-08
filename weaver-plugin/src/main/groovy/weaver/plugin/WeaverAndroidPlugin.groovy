package weaver.plugin

import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverAndroidPlugin extends WeaverPlugin {


    @Override
    void apply(Project project) {
        super.apply(project)
        project.afterEvaluate {
            def variants = getVariants { String name -> project.plugins.findPlugin(name) }
            def preLoaderAdded = false
            project.android[variants].all { variant ->

                if (!preLoaderAdded) {
                    variant.javaCompiler.doLast{
                        getPreLoaderTask(project).execute()
                    }
                    preLoaderAdded = true
                }
            }
        }
    }

    static def getVariants(Closure hasPlugin) {
        if (hasPlugin("com.android.application") || hasPlugin("android") ||
                hasPlugin("com.android.test")) {
            return "applicationVariants"
        } else if (hasPlugin("com.android.library") || hasPlugin("android-library")) {
            return "libraryVariants"
        } else {
            throw new ProjectConfigurationException("The android/android-library plugin must be applied to the project", null)
        }
    }
}
