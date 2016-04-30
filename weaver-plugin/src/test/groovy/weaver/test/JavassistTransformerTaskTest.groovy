package weaver.test

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import weaver.plugin.task.TransformerTask

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class JavassistTransformerTaskTest {

    //build/
    private static
    final File BUILD_ROOT = new File(getClass().getResource("/").toURI()).parentFile.parentFile
    private static
    final File OUTPUT_DIR = new File(BUILD_ROOT.getAbsolutePath() + File.separator + "functionalTest")
    private static
    final File CLASSES_DIR = new File(BUILD_ROOT.getAbsolutePath() + File.separator + "resources" + File.separator + "test" + File.separator + "classes_dir")
//
//    @Test
//    public void test() {
//        Project project = ProjectBuilder.builder().build()
//        project.apply plugin: 'java'
//        project.configurations.create("weaver")
//        project.repositories {
//            jcenter()
//            mavenCentral()
//            maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
//        }
//        project.dependencies {
//            weaver "io.saeid.weaver:sample-processor:0.2-SNAPSHOT"
//        }
//        project.evaluate()
//        JavaCompile compileJavaTask =
//                project.tasks.getByName(project.sourceSets.main.compileJavaTaskName) as JavaCompile
//        TransformerTask task = new TransformerTask.Builder()
//                .setTaskName("weaverTest")
//                .setClasspath(compileJavaTask.classpath)
//                .setOutputDir(OUTPUT_DIR)
//                .setClassesDir(CLASSES_DIR)
//                .build(project)
//        project.tasks.getByName("weaverTest").execute()
//
//    }
}
