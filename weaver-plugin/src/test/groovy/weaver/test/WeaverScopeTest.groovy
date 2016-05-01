package weaver.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import weaver.plugin.internal.util.MetaInfUtils
import weaver.plugin.internal.util.WeaverConfigurationScope

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverScopeTest {

    @Test
    void "Check classes are extracted from META-INF"() {

        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'java'
        project.configurations.create("weaver")
        project.repositories {
            jcenter()
            mavenCentral()
            maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
        }
        project.dependencies {
            weaver "io.saeid.weaver:sample-processor:0.2-SNAPSHOT"
        }
        def jarFiles = WeaverConfigurationScope.getJarFiles(project)
        assert jarFiles
        def names = MetaInfUtils.extractProcessorsName(project, jarFiles)
        assert names.contains("io.saeid.weaver.test.processor.Processor1")
        assert names.contains("io.saeid.weaver.test.processor.Processor2")
    }
}
