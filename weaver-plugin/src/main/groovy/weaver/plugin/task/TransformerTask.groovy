package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import weaver.plugin.internal.util.MetaInfUtils
import weaver.plugin.internal.util.WeaverConfigurationScope
import weaver.processor.WeaverProcessor

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
abstract class TransformerTask extends DefaultTask {

    FileCollection classpath
    /**
     * A folder which .class files exist there.
     */
    @InputDirectory
    File classesDir

    /**
     * Snapshot of manipulated classes.
     */
    @OutputDirectory
    File outputDir

    private List<WeaverProcessor> processors

    @TaskAction
    void startTask() {
        def weaverScopeJarFiles = WeaverConfigurationScope.getJarFiles(project)
        if (!weaverScopeJarFiles) {
            debug("TransformerTask ignored [No weaver dependency specified]")
            return
        }
        def processorsNameInMetaInf = MetaInfUtils.extractProcessorsName(project, weaverScopeJarFiles)
        if (!processorsNameInMetaInf) {
            debug("TransformerTask ignored [No weaver processor specified in META-INF]")
            return
        }

        //weaving
        int time = System.currentTimeMillis()
        weaving()
        int duration = System.currentTimeMillis() - time
        logger.quiet("$name : Weaving takes $duration")
    }

    abstract void weaving()

    void debug(String message) {
        logger.debug(message)
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
            def task = project.task(name, type: JavassistTransformerTask) {
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
