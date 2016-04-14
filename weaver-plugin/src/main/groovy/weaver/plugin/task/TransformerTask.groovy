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

    private def classpath
    @InputDirectory
    private def classesDir
    @OutputDirectory
    private def outputDir

    @TaskAction
    def doTransform() {
        project.logger.quiet("____transforming____")
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
                    from outputDir
                    to classesDir
                }
            }
            return task
        }


    }
}
