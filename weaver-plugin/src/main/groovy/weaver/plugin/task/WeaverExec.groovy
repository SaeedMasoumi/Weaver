package weaver.plugin.task

import javassist.CtClass
import org.gradle.api.logging.Logger
import weaver.common.WeaveEnvironment
import weaver.plugin.model.TransformBundle
import weaver.plugin.processor.ProcessorInstantiator
import weaver.plugin.processor.WeaveEnvironmentImp
import weaver.plugin.util.Disposable

/**
 * <code>WeaverExec</code> is a delegate for all transformer tasks and it's responsible to instantiate and call processors.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverExec implements Disposable {

    TransformBundle bundle
    ProcessorInstantiator processorInstantiator
    Logger logger

    WeaverExec(TransformBundle bundle) {
        this.bundle = bundle
        this.logger = bundle.project.logger
        processorInstantiator = new ProcessorInstantiator(bundle)
    }

    void execute() {
        execute(null)
    }

    void execute(doLast) {
        def processors = processorInstantiator.instantiate()
        def pool = bundle.classPool
        WeaveEnvironment env = new WeaveEnvironmentImp(logger, bundle.classPool, bundle.outputDir)
        processors.each {
            it.init(env)
        }
        def classFiles = bundle.classFiles
        Set<CtClass> classesSet = new HashSet<>();
        classFiles.each {
            classesSet.add(pool.get(it))
        }
        log "${classesSet.size()} class files have been found."
        processors.each {
            try {
                it.transform(classesSet)
            } catch (all) {
                log "Skip processor with class name [${it.class.canonicalName}] \n" +
                        "An error occurred during transformation: \n " +
                        "$all.message "
            }
        }
        if (doLast)
            doLast(classesSet)
    }

    @Override
    void dispose() {
        processorInstantiator.dispose()
    }

    void log(String message) {
        logger.info(message)
    }

    public interface Listener {
        void doLast(Set<CtClass> allClasses)
    }
}
