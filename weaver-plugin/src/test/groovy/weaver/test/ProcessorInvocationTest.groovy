package weaver.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import weaver.common.Processor
import weaver.plugin.internal.processor.ProcessorInvocationHandler

import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessorInvocationTest {

    @Test
    void "Check classes are invoked from META-INF"() {

        Project project = ProjectBuilder.builder().build()
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
        def time = System.currentTimeMillis()
        ProcessorInvocationHandler pih = new ProcessorInvocationHandler(Thread.currentThread().contextClassLoader, project)
        def processors = pih.invokeProcessors(project.configurations.weaver)
        def names = processors.collect({ it.class.canonicalName })
        assertTrue(names.contains("io.saeid.weaver.sample.processor.Processor1"))
        assertTrue(names.contains("io.saeid.weaver.sample.processor.Processor2"))
        processors.each {
            assertThat(it, instanceOf(Processor.class))

        }
        time = System.currentTimeMillis() - time
        println "invocation takes $time millis [note that more than 95-98% of elapsed time is just for configurations.file(dependency)]"
    }
}
