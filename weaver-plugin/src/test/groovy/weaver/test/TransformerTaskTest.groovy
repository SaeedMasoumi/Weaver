package weaver.test

import org.gradle.api.Project
import org.gradle.api.tasks.TaskValidationException
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import weaver.plugin.task.TaskManager
import weaver.plugin.task.TransformerTask

import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TransformerTaskTest {

    Project project
    TransformerTask transformerTask

    @Before
    void "initialize sample project"() {
        project = ProjectBuilder.builder().withProjectDir(Directories.SAMPLE_PROJECT).build()
        project.apply plugin: 'java'
        project.configurations.create("weaver")
        project.repositories {
            jcenter()
            mavenCentral()
            maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
        }
        project.dependencies {
            weaver Dependencies.SAMPLE_PROCESSOR
        }

        executeTask("clean")
        executeTask("compileJava")
        transformerTask = project.getTasks().create("weaverTest", TransformerTask.class)
    }

    def executeTask(String name) {
        project.getTasks().getByPath(name).execute();
    }

    @Test(expected = TaskValidationException.class)
    void "execute task without any configuration"() {
        transformerTask.execute()
    }

    @Test
    void "execute task with given configuration"() {
        JavaCompile compileJavaTask =
                project.tasks.getByName(project.sourceSets.main.compileJavaTaskName) as JavaCompile
        transformerTask.setClasspath(compileJavaTask.classpath)
        transformerTask.setClassesDir(new File(project.buildDir, "classes/main"))
        transformerTask.setOutputDir(new File(project.buildDir, "weaverTest"))
        transformerTask.execute()
        assertTrue(transformerTask.didWork)
        File transformedClass = new File(transformerTask.getOutputDir(), "SampleClass.class")
        assertTrue(transformedClass.exists())
    }

    @Test
    void "creating task with TaskManager and execute it"() {
        JavaCompile compileJavaTask =
                project.tasks.getByName(project.sourceSets.main.compileJavaTaskName) as JavaCompile
        TransformerTask transformerTask2 = TaskManager.createTransformerTask(
                project,
                "weaverTest2",
                compileJavaTask.classpath,
                new File(project.buildDir, "classes/main"),
                new File(project.buildDir, "weaver")
        ) as TransformerTask
        executeTask("weaverTest2")
        assertTrue(transformerTask2.didWork)
        File transformedClass = new File(transformerTask2.getOutputDir(), "SampleClass.class")
        assertTrue(transformedClass.exists())

        ClassLoader cl = transformerTask2.getClassLoader()
        Object sampleClass = cl.loadClass("SampleClass").newInstance()
        assertThat(sampleClass, instanceOf(Runnable.class))
        Runnable runnable = sampleClass as Runnable
        runnable.run()
        executeTask("clean")
        executeTask("build")
    }
}
