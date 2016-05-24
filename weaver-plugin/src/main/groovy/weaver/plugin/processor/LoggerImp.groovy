package weaver.plugin.processor

import groovy.transform.CompileStatic
import weaver.common.Logger

/**
 * A concrete implementation of {@link Logger}. It uses {@link org.gradle.api.Project#getLogger()}.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@CompileStatic
class LoggerImp implements Logger {

    private org.gradle.api.logging.Logger logger

    public LoggerImp(org.gradle.api.logging.Logger logger) {
        this.logger = logger
    }

    @Override
    void debug(String name) {
        logger.debug(name)
    }

    @Override
    void quiet(String name) {
        logger.quiet(name)
    }

    @Override
    void info(String name) {
        logger.info(name)
    }

    @Override
    void warning(String name) {
        logger.warn(name)
    }
}
