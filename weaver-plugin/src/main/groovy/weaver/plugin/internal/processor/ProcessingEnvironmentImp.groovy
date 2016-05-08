package weaver.plugin.internal.processor

import org.gradle.api.Project
import weaver.plugin.internal.javassist.WeaverClassPool
import weaver.common.Logger
import weaver.common.WeaveEnvironment
import weaver.instrumentation.InstrumentationImp

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessingEnvironmentImp implements WeaveEnvironment {
    private Logger logger
    private InstrumentationImp weaverToolkit;

    ProcessingEnvironmentImp(Project project, WeaverClassPool pool) {
        logger = new LoggerImp(project.logger)
        weaverToolkit = new InstrumentationImp(pool)
    }

    @Override
    Logger getLogger() {
        return logger
    }

    @Override
    InstrumentationImp getWeaverToolkit() {
        return weaverToolkit
    }
}
