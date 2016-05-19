package weaver.plugin.processor

import groovy.transform.CompileStatic
import javassist.ClassPool
import weaver.common.Logger
import weaver.common.WeaveEnvironment
import weaver.plugin.javassist.WeaverClassPool

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@CompileStatic
class WeaveEnvironmentImp implements WeaveEnvironment {
    private Logger logger
    private ClassPool pool
    private File outputDir

    WeaveEnvironmentImp(org.gradle.api.logging.Logger logger, WeaverClassPool pool, File outputDir) {
        this.logger = new LoggerImp(logger)
        this.pool = pool
        this.outputDir = outputDir
    }

    @Override
    Logger getLogger() {
        return logger
    }

    @Override
    ClassPool getClassPool() {
        return pool
    }

    @Override
    File getOutputDir() {
        return outputDir
    }
}
