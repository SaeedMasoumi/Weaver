package weaver.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverConfigurationTest {

    @Test
    public void test() {
        Project project = ProjectBuilder.builder().build()
        project.file(".").mkdir();
        project.apply plugin: 'java'
        project.apply plugin: 'weaver'
        project.dependencies {
            weaver "org.javassist:javassist:3.20.0-GA"
        }

        project.evaluate()
    }
}
