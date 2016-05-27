package weaver.test

import com.google.common.collect.ImmutableSet
import javassist.CtClass
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import weaver.plugin.javassist.WeaverClassPool
import weaver.plugin.model.TransformBundle
import weaver.plugin.model.TransformBundleImp
import weaver.plugin.transform.TransformerDelegate

import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue
import static weaver.plugin.util.UrlUtils.normalizeDirectoryForClassLoader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TransformerDelegateTest {
    TransformBundle bundle
    Project project

    @Before
    void "initialize sample project"() {
        project = ProjectBuilder.builder().withProjectDir(Directories.SAMPLE_PROJECT).build()
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
        def urls = []
        def classLoader = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
        Set<File> classFiles = ImmutableSet.<File> of(project.file("$project.buildDir/classes/main/SampleClass.class"))
        def pool = new WeaverClassPool(classLoader)
        bundle = TransformBundleImp
                .builder()
                .project(project)
                .configuration(project.configurations.getByName("weaver"))
                .rootClassLoader(classLoader)
                .classPool(pool)
                .classFiles(classFiles)
                .outputDir(project.file("$project.buildDir/weaver/main"))
                .build()
        executeTask("clean")
        executeTask("compileJava")
        executeTask("classes")
    }


    @Test
    void "test"() {
        TransformerDelegate pi = new TransformerDelegate(bundle)
        boolean doLastCalled = false
        pi.execute() { Set<CtClass> allClasses ->
            allClasses.each { println it.name }
            doLastCalled = true
        }
        assertTrue(doLastCalled)
        def urls = []
        urls += normalizeDirectoryForClassLoader(bundle.getOutputDir())
        ClassLoader cl = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
        Object sampleClass = cl.loadClass("SampleClass").newInstance()
        assertThat(sampleClass, instanceOf(Runnable.class))
        Runnable runnable = sampleClass as Runnable
        runnable.run()
    }

    def executeTask(String name) {
        project.getTasks().getByName(name).execute();
    }
}
