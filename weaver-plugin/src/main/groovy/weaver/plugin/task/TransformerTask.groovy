package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.compile.JavaCompile

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
abstract class TransformerTask extends DefaultTask {


    private JavaCompile javaCompile
    @InputDirectory
    private FileCollection classPath

    @TaskAction
    def doTransform() {
        loadClassLoader()
        transform()
    }


}
