package weaver.plugin.task

import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import weaver.plugin.internal.processor.ProcessingEnvironmentImp
import weaver.plugin.internal.processor.ProcessorLoader
import weaver.processor.ProcessingEnvironment
import weaver.processor.WeaverProcessor

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class TransformerTask extends DefaultTask {

    /**
     * Contains all .jar files and classes folder of source set related to this project.
     */
    private FileCollection classpath
    /**
     * A folder which .class files live there.
     */
    private File classesDir
    /**
     * Snapshot of manipulated classes.
     */
    private File outputDir

    @TaskAction
    def initTask() {
        //TODO ignore if classes dir is empty
        int time = System.currentTimeMillis()
        weaving()
        int duration = System.currentTimeMillis() - time
        logger.debug("$name : Weaving takes $duration")
    }

    def weaving() {
        logger.debug("$name : Start weaving")
        def weaverProcessors = getProcessors()
        ProcessingEnvironment env = getProcessingEnvironment()
        //init processors
        weaverProcessors.forEach {
            it.init(env)
        }
        final ClassPool pool = createPool();
        getClasses().forEach {
            CtClass ctClass = loadClassFile(pool, it)
            weaverProcessors.forEach {
                if (it.filter(ctClass)) {
                    ctClass.defrost()
                    it.apply(ctClass)
                    ctClass.writeFile(outputDir.path)
                }
            }
        }

    }

    ProcessingEnvironment getProcessingEnvironment() {
        return new ProcessingEnvironmentImp(project)
    }

    ClassPool createPool() {
        ClassPool pool = new ClassPool(true)
        if (classpath) {
            classpath.forEach {
                pool.appendClassPath(it.toString())
            }
        }
        return pool
    }

    static CtClass loadClassFile(ClassPool pool, File classFile) throws IOException {
        InputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(classFile)));
        CtClass clazz = pool.makeClass(stream);
        stream.close();
        return clazz;
    }

    ArrayList<WeaverProcessor> getProcessors() {
        return new ProcessorLoader(project, project.configurations.weaver.files).getProcessors()
    }

    /**
     * @return Returns all .class files from build directory.
     */
    Set<File> getClasses() {
        return project.fileTree(classesDir).matching {
            include '**/*.class'
        }.files
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

    public FileCollection getClasspath() {
        return classpath
    }

    public void setClasspath(FileCollection classpath) {
        this.classpath = classpath
    }

    @InputDirectory
    public File getClassesDir() {
        return classesDir
    }

    public void setClassesDir(File classesDir) {
        this.classesDir = classesDir
    }

    @OutputDirectory
    public File getOutputDir() {
        return outputDir
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir
    }
}
