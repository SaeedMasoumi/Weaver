package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import weaver.plugin.internal.ProcessorLoader
import weaver.processor.WeaverProcessor

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class TransformerTask extends DefaultTask {

    @InputDirectory
    private FileCollection classpath
    private File classesDir
    @OutputDirectory
    private File outputDir

    @TaskAction
    def doTransform() {
        if (classesDir.isDirectory()) {
            if (classesDir.length() == 0) {
                logger.warn("$name: $classesDir.path is empty, weaving ingored")
            }
        }
        def weaverProcessors = getProcessors()
    }

    ArrayList<WeaverProcessor> getProcessors() {
        return new ProcessorLoader(project, project.configurations.weaver.files).getProcessors()
    }

    public static class Builder {
        FileCollection classpath
        File classesDir
        File outputDir
        String name

        public Builder setClasspath(def classpath) {
            this.classpath = classpath
            return this
        }

        public Builder setClassesDir(def classesDir) {
            this.classesDir = classesDir
            return this
        }

        public Builder setOutputDir(def outputDir) {
            this.outputDir = outputDir
            return this
        }

        public Builder setTaskName(def name) {
            this.name = name
            return this
        }

        public Task build(Project project) {
            def task = project.task(name, type: TransformerTask) {
                classpath = this.classpath
                classesDir = this.classesDir
                outputDir = this.outputDir
            }
            task.doLast {
                project.copy {
                    from outputDir.path
                    into classesDir.path
                }
            }
            return task
        }

    }
}
