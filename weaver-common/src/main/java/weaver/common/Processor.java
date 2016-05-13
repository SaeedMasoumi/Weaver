package weaver.common;

import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Processor {

    void init(WeaveEnvironment processingEnvironment);

    boolean filter(CtClass candidateClass);

    void transform(CtClass candidateClass) throws Exception;
}
