package weaver.plugin.processor

import javassist.CtClass
import weaver.common.WeaveEnvironment
import weaver.plugin.model.TransformBundle
import weaver.plugin.util.Disposable

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessorInvocator implements Disposable {

    TransformBundle bundle
    ProcessorInstantiator processorInstantiator

    ProcessorInvocator(TransformBundle bundle) {
        this.bundle = bundle
        processorInstantiator = new ProcessorInstantiator(bundle)
    }

    void execute() {
        def processors = processorInstantiator.instantiate()
        if (!processors)
            return
        def pool = bundle.classPool
        WeaveEnvironment env = new WeaveEnvironmentImp(bundle.project.logger, bundle.classPool, bundle.outputDir)
        processors.each {
            it.init(env)
        }
        def classFiles = bundle.classFiles
        Set<CtClass> classesSet = new HashSet<>();
        classFiles.each {
            classesSet.add(pool.get(it))
        }
        processors.each {
            it.transform(classesSet)
        }
    }

    @Override
    void dispose() {
        processorInstantiator.dispose()
    }
}
