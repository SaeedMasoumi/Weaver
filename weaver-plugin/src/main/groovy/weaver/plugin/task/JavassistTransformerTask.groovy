package weaver.plugin.task

import javassist.ClassPool
import javassist.CtClass
import weaver.plugin.internal.javassist.WeaverClassPool
import weaver.plugin.internal.processor.ProcessingEnvironmentImp
import weaver.processor.ProcessingEnvironment

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class JavassistTransformerTask extends TransformerTask {

    WeaverClassPool pool

    @Override
    void weaving() {
        createPool();
        ProcessingEnvironment env = getProcessingEnvironment(pool)
        //init processors
        processors.each {
            it.init(env)
        }
        classesFiles.each {
            CtClass ctClass = pool.get(it)
            processors.each {
                if (it.filter(ctClass)) {
                    ctClass.defrost()
                    it.process(ctClass)
                    ctClass.writeFile(outputDir.path)
                }
            }
        }
    }

    ProcessingEnvironment getProcessingEnvironment(WeaverClassPool pool) {
        new ProcessingEnvironmentImp(project, pool);
    }

    void createPool() {
        pool = new WeaverClassPool(classLoader, true)
        pool.appendClassPath(classpath)
        pool.appendClassPath(classesDir)
        pool.appendClassPath(weaverScopeClasspath)
    }
}
