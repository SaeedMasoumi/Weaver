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
        File file = new File(System.getProperty("user.dir"))
        def localMaven = file.getParentFile().getAbsolutePath() + "/localmaven"
        Project project = ProjectBuilder.builder().build()
        project.file(".").mkdir();
        project.apply plugin: 'java'
        project.apply plugin: 'weaver'
        project.repositories {
            maven {
                url localMaven
            }
            jcenter()
            mavenCentral()
        }
        project.dependencies {
            weaver "io.reactivex:rxjava:1.0.11"
            weaver "com.google.dagger:dagger:2.2"
        }
        project.evaluate()
    }
}
