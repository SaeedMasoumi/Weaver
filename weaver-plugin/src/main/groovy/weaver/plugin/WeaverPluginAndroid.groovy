package weaver.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
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
            def isLibrary = project.plugins.hasPlugin(LibraryPlugin)
            if (isLibrary) {
                def android = project.extensions.getByType(LibraryExtension)
                android.libraryVariants.all { BaseVariant variant ->
                    configure project, variant, isLibrary
                }
            } else {
                def android = project.extensions.getByType(AppExtension)
                android.applicationVariants.all { BaseVariant variant ->
                    configure project, variant, isLibrary
                }
            }
        }
    }

    static void configure(Project project, BaseVariant variant, boolean isLibrary) {
        def taskName = "$TRANSFORMER_TASK${variant.name.capitalize()}"
        JavaCompile javaCompileTask = variant.javaCompiler as JavaCompile
        //TODO it kills jack & jill
        FileCollection classpathFileCollection = project.files(javaCompileTask.options.bootClasspath)
        classpathFileCollection += javaCompileTask.classpath
        //TODO pass exclude type for .class files (e.g. R.class)
        //TODO source set should be included into classpath
        def transformerTask =
                new TransformerTask.Builder()
                        .setClassesDir(javaCompileTask.destinationDir)
                        .setClasspath(classpathFileCollection)
                        .setOutputDir(project.file("$project.buildDir/intermediates/weaver/$variant.name"))
                        .setTaskName(taskName)
                        .build(project)
        transformerTask.mustRunAfter javaCompileTask
        variant.assemble.dependsOn transformerTask
        if (!isLibrary) variant.install?.dependsOn(transformerTask)
    }

}
