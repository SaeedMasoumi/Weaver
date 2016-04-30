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

    ClassLoader classLoader

    Set<File> weaverScopeClasspath

    List<WeaverProcessor> processors

    Set<File> classesFiles

    @TaskAction
    void startTask() {
        weaverScopeClasspath = WeaverConfigurationScope.getJarFiles(project)
        if (!weaverScopeClasspath) {
            debug("TransformerTask ignored [No weaver dependency specified]")
            return
        }
        def processorsNameInMetaInf = MetaInfUtils.extractProcessorsName(project, weaverScopeClasspath)
        if (!processorsNameInMetaInf) {
            debug("TransformerTask ignored [No weaver processor specified in META-INF]")
            return
        }
        classesFiles = getClassesFiles()
        classLoader = initClassLoader()
        processors = initWeaverProcessors(processorsNameInMetaInf)

        //weaving
        int time = System.currentTimeMillis()
        weaving()
        int duration = System.currentTimeMillis() - time
        logger.quiet("$name : Weaving takes $duration")
    }

    ClassLoader initClassLoader() {
        def urls = weaverScopeClasspath.collect() { it.toURI().toURL() }
        urls += classpath.getFiles().collect { it.toURI().toURL() }
        URLClassLoader classLoader = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
        Thread.currentThread().contextClassLoader = classLoader
        return classLoader
    }

    List<WeaverProcessor> initWeaverProcessors(List<String> names) {
        List<WeaverProcessor> processors = new ArrayList<>()
        names.each {
            processors.add(classLoader.loadClass(it).newInstance() as WeaverProcessor)
        }
        return processors
    }

    /**
     * @return Returns all .class files from build directory.
     */
    Set<File> getClassesFiles() {
        return project.fileTree(classesDir).matching {
            include '**/*.class'
        }.files
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
