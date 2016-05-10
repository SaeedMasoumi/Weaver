package weaver.plugin.internal.processor

import groovy.transform.CompileStatic
import javassist.ClassPool
import org.gradle.api.Project
import weaver.common.Logger
import weaver.common.WeaveEnvironment
import weaver.plugin.internal.javassist.WeaverClassPool

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@CompileStatic
class WeaveEnvironmentImp implements WeaveEnvironment {
    private Logger logger
    private ClassPool pool

    WeaveEnvironmentImp(Project project, WeaverClassPool pool) {
        logger = new LoggerImp(project.logger)
        this.pool = pool
    }

    @Override
    Logger getLogger() {
        return logger
    }

    @Override
    ClassPool getClassPool() {
        return pool
    }
}
