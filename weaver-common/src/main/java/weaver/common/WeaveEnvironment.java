package weaver.common;

import javassist.ClassPool;

/**
 * The {@link Processor} can use facilities provided by this interface during bytecode manipulation.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface WeaveEnvironment {
    /**
     * @return Returns the logger.
     */
    Logger getLogger();

    ClassPool getClassPool();
}
