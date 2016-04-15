package weaver.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import weaver.plugin.internal.processor.ProcessorLoader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessorExtractionTest {

    @Test
    void "Check classes are loaded from META-INF"() {

        Project project = ProjectBuilder.builder().build()
        project.file(".").mkdir();
        project.apply plugin: 'java'
        project.repositories {
            jcenter()
            mavenCentral()
            maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
        }
        project.dependencies {
            compile "io.saeid.weaver:sample-processor:0.2-SNAPSHOT"
        }
        
        ProcessorLoader loader = new ProcessorLoader(project, project.configurations.compile.files)
        def processors = loader.getProcessors()
        def names = processors.collect({ it.getClass().getCanonicalName() })
        assert names.contains("io.saeid.weaver.test.processor.Processor1")
        assert names.contains("io.saeid.weaver.test.processor.Processor2")
    }
}
