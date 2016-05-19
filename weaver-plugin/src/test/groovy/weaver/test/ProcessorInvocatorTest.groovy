package weaver.test

import com.google.common.collect.ImmutableSet
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import weaver.plugin.javassist.WeaverClassPool
import weaver.plugin.model.TransformBundle
import weaver.plugin.model.TransformBundleImp
import weaver.plugin.processor.ProcessorInvocator

import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat
import static weaver.plugin.util.UrlUtils.normalizeDirectoryForClassLoader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessorInvocatorTest {
    TransformBundle bundle

    @Before
    void "initialize sample project"() {
        Project project = ProjectBuilder.builder().withProjectDir(Directories.SAMPLE_PROJECT).build()
        project.apply plugin: 'java'
        project.configurations.create("weaver")
        project.repositories {
            jcenter()
            mavenCentral()
            maven { url "https://oss.jfrog.org/oss-snapshot-local" }
        }
        project.dependencies {
            weaver Dependencies.SAMPLE_PROCESSOR
        }
        project.evaluate()
        bundle = new TransformBundleImp()
        bundle.setProject(project)
        bundle.setOutputDir(project.file("$project.buildDir/weaver/main"))
        bundle.setConfiguration(project.configurations.getByName("weaver"))
        def urls = []
        bundle.setRootClassLoader(new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader))
        bundle.setClassPool(new WeaverClassPool(bundle.getRootClassLoader()))
        bundle.setClassFiles(ImmutableSet.<File> of(project.file("$project.buildDir/weaver/main/SampleClass.class")))
    }


    @Test
    void "test"() {
        ProcessorInvocator pi = new ProcessorInvocator(bundle)
        pi.execute()
        def urls = []
        urls += normalizeDirectoryForClassLoader(bundle.getOutputDir())
        ClassLoader cl = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
        Object sampleClass = cl.loadClass("SampleClass").newInstance()
        assertThat(sampleClass, instanceOf(Runnable.class))
        Runnable runnable = sampleClass as Runnable
        runnable.run()
    }

}
