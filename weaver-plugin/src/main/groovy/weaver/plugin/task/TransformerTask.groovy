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
import weaver.plugin.internal.ProcessorLoader
import weaver.processor.WeaverProcessor

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class TransformerTask extends DefaultTask {

    private FileCollection classpath
    @InputDirectory
    private File classesDir
    @OutputDirectory
    private File outputDir

    @TaskAction
    def initTask() {
        if (classesDir.isDirectory()) {
            if (classesDir.length() == 0) {
                logger.warn("$name: $classesDir.path is empty, weaving ingored")
                return
            }
        }
        weaving()
    }

    def weaving() {
        def weaverProcessors = getProcessors()
        //TODO get processing env
        //init processors
        weaverProcessors.forEach {
            it.init(null)
        }
        final ClassPool pool = createPool();
        getClasses().forEach {
            CtClass ctClass = loadClassFile(pool, it)
            weaverProcessors.forEach {
                if (it.filter(ctClass)) {
                    ctClass.defrost()
                    it.apply(ctClass)
                }
            }
            ctClass.writeFile(outputDir.path)
        }
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
}
