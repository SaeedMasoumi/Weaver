package weaver.plugin

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.compile.JavaCompile
import weaver.plugin.task.TransformerTask

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverPluginAndroid implements Plugin<Project> {

    static final TRANSFORMER_TASK = "weaverAndroid"

    @Override
    void apply(Project project) {
        project.afterEvaluate {
            def variants = getVariants { String name -> project.plugins.findPlugin(name) }
            project.android[variants].all { BaseVariant variant ->
                //TODO check whether variant has been rejected or not
                def taskName = "$TRANSFORMER_TASK${variant.name.capitalize()}"
                JavaCompile javaCompileTask = variant.javaCompiler as JavaCompile
                FileCollection classpathFileCollection = project.files(javaCompileTask.options.bootClasspath)
                classpathFileCollection += javaCompileTask.classpath

                def transformerTask =
                        new TransformerTask.Builder()
                                .setClassesDir(javaCompileTask.destinationDir)
                                .setClasspath(classpathFileCollection)
                                .setOutputDir(project.file("$project.buildDir/intermediates/weaver/$variant.name"))
                                .setTaskName(taskName)
                                .build(project)
                transformerTask.mustRunAfter javaCompileTask
                variant.assemble.dependsOn transformerTask
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
