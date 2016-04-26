package weaver.plugin.internal.processor

import javassist.ClassPool
import org.gradle.api.Project
import weaver.plugin.internal.processor.injector.TemplateInjectorImp
import weaver.processor.Logger
import weaver.processor.ProcessingEnvironment
import weaver.processor.injector.TemplateInjector

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessingEnvironmentImp implements ProcessingEnvironment {
    private Logger logger
    private TemplateInjector templateInjector

    ProcessingEnvironmentImp(Project project, ClassPool pool) {
        logger = new LoggerImp(project.logger)
        templateInjector = new TemplateInjectorImp(pool);
    }

    @Override
    Logger getLogger() {
        return logger
    }

    @Override
    TemplateInjector getTemplateInjector() {
        return templateInjector
    }
}
