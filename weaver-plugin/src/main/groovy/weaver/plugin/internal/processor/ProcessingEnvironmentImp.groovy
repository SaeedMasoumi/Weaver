package weaver.plugin.internal.processor

import org.gradle.api.Project
import weaver.processor.Logger
import weaver.processor.ProcessingEnvironment

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessingEnvironmentImp implements ProcessingEnvironment {
    private Logger logger

    ProcessingEnvironmentImp(Project project) {
        logger = new LoggerImp(project.logger)
    }

    @Override
    Logger getLogger() {
        return logger
    }
}
