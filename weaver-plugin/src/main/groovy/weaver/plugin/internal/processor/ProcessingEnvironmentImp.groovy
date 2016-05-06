package weaver.plugin.internal.processor

import org.gradle.api.Project
import weaver.plugin.internal.javassist.WeaverClassPool
import weaver.processor.Logger
import weaver.processor.ProcessingEnvironment
import weaver.toolkit.WeaverToolkit

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessingEnvironmentImp implements ProcessingEnvironment {
    private Logger logger
    private WeaverToolkit weaverToolkit;

    ProcessingEnvironmentImp(Project project, WeaverClassPool pool) {
        logger = new LoggerImp(project.logger)
        weaverToolkit = new WeaverToolkit(pool)
    }

    @Override
    Logger getLogger() {
        return logger
    }

    @Override
    WeaverToolkit getWeaverToolkit() {
        return weaverToolkit
    }
}
