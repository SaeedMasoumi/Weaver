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

    private FileCollection classpath
    /**
     * A folder which .class files exist there.
     */
    private File classesDir
    /**
     * Snapshot of manipulated classes.
     */
    private File outputDir

    @TaskAction
    def initTask() {
        logger.quiet("Start weaving (classes directory:$classesDir.name  output directory:$outputDir.name")
        //TODO ignore if classes dir is empty
        int time = System.currentTimeMillis()
        weaving()
        int duration = System.currentTimeMillis() - time
        logger.quiet("$name : Weaving takes $duration")
    }

    def weaving() {
        final ClassPool pool = createPool();
        def weaverProcessors = getProcessors()
        ProcessingEnvironment env = getProcessingEnvironment(pool)
        //init processors
        weaverProcessors.each {
            it.init(env)
        }
        getClassesFiles().each {
            CtClass ctClass = loadClassFile(pool, it)
            weaverProcessors.each {
                if (it.filter(ctClass)) {
                    ctClass.defrost()
                    it.process(ctClass)
                    ctClass.writeFile(outputDir.path)
                }
            }
        }

    }

    ProcessingEnvironment getProcessingEnvironment(ClassPool pool) {
        new ProcessingEnvironmentImp(project, pool);
    }

    ClassPool createPool() {
        ClassPool pool = new ClassPool(true)
        if (classpath) {
            classpath.each {
                pool.appendClassPath(it.toString())
            }
        }
        pool.appendClassPath(classesDir.toString())
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
    Set<File> getClassesFiles() {
        return project.fileTree(classesDir).matching {
            include '**/*.class'
        }.files
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
