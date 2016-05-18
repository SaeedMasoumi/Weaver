package weaver.plugin.task

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile

import static weaver.plugin.WeaverPlugin.TEST_WEAVER_CONFIGURATION
import static weaver.plugin.WeaverPlugin.WEAVER_CONFIGURATION

/**
 * Manages tasks creation.
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TaskBuilder {
    public static final String TASK_PREFIX = "weaver"

    /**
     * Creates a transformer task for default android toolchain (not jack & jill)
     */
    static def configureAndroidTransformerTask(Project project, BaseVariant variant) {
        def taskName = "$TASK_PREFIX${variant.name.capitalize()}Android"
        def transformerTask = createAndroidTransformerTask(project, variant, taskName, WEAVER_CONFIGURATION)
        //also run transformer task before unit test
        def unitTest = project.tasks.find { task ->
            task.name.startsWith("compile${variant.name.capitalize()}UnitTestJava")
        }
        if (unitTest)
            unitTest.dependsOn transformerTask

        return transformerTask
    }

    static def configureAndroidTestTransformerTask(Project project, BaseVariant variant) {
        def taskName = "$TASK_PREFIX${variant.name.capitalize()}"
        def transformerTask = createAndroidTransformerTask(project, variant, taskName, TEST_WEAVER_CONFIGURATION)
        return transformerTask
    }

    private static
    def createAndroidTransformerTask(Project project, BaseVariant variant, String taskName, String configurationName) {
        JavaCompile javaCompileTask = variant.javaCompiler as JavaCompile
        FileCollection classpathFileCollection = project.files(javaCompileTask.options.bootClasspath)
        classpathFileCollection += javaCompileTask.classpath
        //TODO pass exclude type for .class files (e.g. R.class)

        def transformerTask = createTransformerTask(
                project,
                taskName,
                classpathFileCollection,
                javaCompileTask.destinationDir,
                project.file("$project.buildDir/weaver/$variant.name"),
                configurationName
        )

        transformerTask.mustRunAfter javaCompileTask
        variant.assemble.dependsOn transformerTask
        return transformerTask
    }

    /**
     * Creates a transformer task for plain java projects
     */
    static def configureJavaTransformerTask(Project project, SourceSet set) {
        def taskName = "$TASK_PREFIX${set.name.capitalize()}Java"
        def transformerTask = createTransformerTask(
                project,
                taskName,
                set.compileClasspath,
                set.output.classesDir,
                project.file("$project.buildDir/weaver/$set.name"),
                WEAVER_CONFIGURATION
        )
        def compileJavaTask = project.tasks.getByName(set.compileJavaTaskName)
        def classesTask = project.tasks.getByName(set.classesTaskName)
        transformerTask.mustRunAfter compileJavaTask
        classesTask.doLast {
            transformerTask.execute()
        }
    }

    static def createTransformerTask(Project project, String name,
                                     FileCollection givenClasspath, File givenClassesDir,
                                     File givenOutputDir, String configuration) {
        def task = project.task(name, type: TransformerTask) {
            classpath = givenClasspath
            classesDir = givenClassesDir
            outputDir = givenOutputDir
            configurationName = configuration
            outputs.upToDateWhen {
                false
            }
        }
        task.doLast {
            project.copy {
                from givenOutputDir.path
                into givenClassesDir.path
            }
        }
        return task
    }
}
