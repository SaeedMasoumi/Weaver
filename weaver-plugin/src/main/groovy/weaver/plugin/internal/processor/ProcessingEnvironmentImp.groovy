package weaver.plugin.internal.processor

import org.gradle.api.Project
import weaver.plugin.internal.javassist.WeaverClassPool
import weaver.plugin.internal.processor.injector.JavassistTemplateInjector
import weaver.plugin.internal.processor.toolkit.JavassistToolkit
import weaver.processor.Logger
import weaver.processor.ProcessingEnvironment
import weaver.processor.TemplateInjector
import weaver.processor.Toolkit

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessingEnvironmentImp implements ProcessingEnvironment {
    private Logger logger
    private TemplateInjector templateInjector
    private Toolkit toolkit

    ProcessingEnvironmentImp(Project project, WeaverClassPool pool) {
        logger = new LoggerImp(project.logger)
        templateInjector = new JavassistTemplateInjector(pool)
        toolkit = new JavassistToolkit()
    }

    @Override
    Logger getLogger() {
        return logger
    }

    @Override
    TemplateInjector getTemplateInjector() {
        return templateInjector
    }

    @Override
    Toolkit getToolkit() {
        return toolkit
    }
}
