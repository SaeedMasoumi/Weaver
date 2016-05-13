package weaver.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import weaver.plugin.WeaverPlugin
import weaver.plugin.task.TransformerTask

import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class JavaPluginTest {
    Project project

    @Before
    void "initialize sample project"() {
        project = ProjectBuilder.builder().withProjectDir(Directories.SAMPLE_PROJECT).build()
        project.apply plugin: 'java'
        project.apply plugin: WeaverPlugin
        project.repositories {
            jcenter()
            mavenCentral()
            maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
        }
        project.dependencies {
            weaver Dependencies.SAMPLE_PROCESSOR
        }
        project.evaluate()
    }

    @Test
    void "WeaverPluginJava must transform SampleClass"() {
        executeTask("clean")
        executeTask("compileJava")
        executeTask("classes")
        TransformerTask transformerTask = getTask("weaverJavaMain") as TransformerTask
        assertTrue(transformerTask.didWork)
        File transformedClass = new File(transformerTask.getOutputDir(), "SampleClass.class")
        assertTrue(transformedClass.exists())

        ClassLoader cl = transformerTask.getClassLoader()
        Object sampleClass = cl.loadClass("SampleClass").newInstance()
        assertThat(sampleClass, instanceOf(Runnable.class))
        Runnable runnable = sampleClass as Runnable
        runnable.run()
    }


    def executeTask(String name) {
        project.getTasks().getByName(name).execute();
    }

    def getTask(String name) {
        return project.getTasks().getByName(name)
    }
}
