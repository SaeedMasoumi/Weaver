package weaver.plugin

import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import weaver.plugin.task.AndroidTransformerTask

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverAndroidPlugin extends WeaverPlugin {

    static final TRANSFORMER_TASK = "weaverAndroid"

    @Override
    void apply(Project project) {
        super.apply(project)
        project.afterEvaluate {
            def variants = getVariants { String name -> project.plugins.findPlugin(name) }
            project.android[variants].all { variant ->
                //TODO check whether variant has been rejected or not
                def taskName = "$TRANSFORMER_TASK$variant.name"
                project.task(taskName, type: AndroidTransformerTask)
                variant.javaCompiler.doLast {
                    getTask(project, taskName).execute()
                }
                project.logger.quiet("Task $taskName added after $variant.javaCompiler.name")
                getTask(project, taskName).mustRunAfter variant.javaCompiler
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
