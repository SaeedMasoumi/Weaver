package weaver.plugin.internal.processor

import weaver.common.Logger

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
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
