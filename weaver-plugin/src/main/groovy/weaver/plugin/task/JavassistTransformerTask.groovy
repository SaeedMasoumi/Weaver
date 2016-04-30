package weaver.plugin.task

import javassist.ClassPool
import javassist.CtClass
import weaver.plugin.internal.processor.ProcessingEnvironmentImp
import weaver.plugin.internal.util.ProcessorLoader
import weaver.processor.ProcessingEnvironment
import weaver.processor.WeaverProcessor

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class JavassistTransformerTask extends TransformerTask {

    @Override
    void weaving() {
        final ClassPool pool = createPool();
        def weaverProcessors = null
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

//    ArrayList<WeaverProcessor> getProcessors() {
//        return new ProcessorLoader(project, project.configurations.weaver.files).getProcessors()
//    }

    /**
     * @return Returns all .class files from build directory.
     */
    Set<File> getClassesFiles() {
        return project.fileTree(classesDir).matching {
            include '**/*.class'
        }.files
    }

}
