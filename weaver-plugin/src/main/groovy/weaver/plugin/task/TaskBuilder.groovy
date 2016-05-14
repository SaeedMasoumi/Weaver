package weaver.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.TestVariant
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile

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
        JavaCompile javaCompileTask = variant.javaCompiler as JavaCompile
        FileCollection classpathFileCollection = project.files(javaCompileTask.options.bootClasspath)
        classpathFileCollection += javaCompileTask.classpath
        //TODO pass exclude type for .class files (e.g. R.class)

        def transformerTask = createTransformerTask(
                project,
                taskName,
                classpathFileCollection,
                javaCompileTask.destinationDir,
                project.file("$project.buildDir/weaver/$variant.name")
        )

        transformerTask.mustRunAfter javaCompileTask
        variant.assemble.dependsOn transformerTask
        //also run transformer task before unit tests
        project.tasks.whenTaskAdded { task ->
            if (task.name.startsWith("compile${variant.name.capitalize()}UnitTestJava")) {
                task.dependsOn transformerTask
            }
        }
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
                project.file("$project.buildDir/weaver/$set.name")
        )
        def compileJavaTask = project.tasks.getByName(set.compileJavaTaskName)
        def classesTask = project.tasks.getByName(set.classesTaskName)
        transformerTask.mustRunAfter compileJavaTask
        classesTask.doLast {
            transformerTask.execute()
        }
    }

    static def createTransformerTask(Project project, String name,
                                     FileCollection givenClasspath, File givenClassesDir, File givenOutputDir) {
        def task = project.task(name, type: TransformerTask) {
            classpath = givenClasspath
            classesDir = givenClassesDir
            outputDir = givenOutputDir
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
