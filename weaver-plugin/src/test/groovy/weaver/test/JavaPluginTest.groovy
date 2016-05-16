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
import static weaver.plugin.internal.util.UrlUtils.normalizeDirectoryForClassLoader

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
            maven { url "https://oss.jfrog.org/oss-snapshot-local" }
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
        TransformerTask transformerTask = getTask("weaverMainJava") as TransformerTask
        assertTrue(transformerTask.didWork)
        File transformedClass = new File(transformerTask.getOutputDir(), "SampleClass.class")
        assertTrue(transformedClass.exists())

        def urls = []
        urls += normalizeDirectoryForClassLoader(transformerTask.getOutputDir())
        ClassLoader cl = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
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
