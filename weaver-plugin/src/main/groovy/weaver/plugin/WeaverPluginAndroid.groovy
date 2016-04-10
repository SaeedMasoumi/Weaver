package weaver.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.compile.JavaCompile
import weaver.plugin.task.AndroidTransformerTask

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverPluginAndroid implements Plugin<Project> {

    static final TRANSFORMER_TASK = "weaverAndroid"

    @Override
    void apply(Project project) {
        super.apply(project)
        project.afterEvaluate {
            def variants = getVariants { String name -> project.plugins.findPlugin(name) }
            project.android[variants].all { variant ->
                //TODO check whether variant has been rejected or not
                def taskName = "$TRANSFORMER_TASK${variant.name.capitalize()}"
                JavaCompile androidJavaCompile = variant.javaCompile
                FileCollection classpathFileCollection = project.files(project.android.bootClasspath)
                classpathFileCollection += androidJavaCompile.classpath

                def transformerTask = project.task(taskName, type: AndroidTransformerTask) {
                    javaCompile = androidJavaCompile
                    classPath = classpathFileCollection
                }
                transformerTask.mustRunAfter androidJavaCompile
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
