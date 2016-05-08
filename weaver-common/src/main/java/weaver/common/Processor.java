package weaver.common;

import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Processor {

    void init(WeaveEnvironment processingEnvironment);

    boolean filter(CtClass ctClass);

    void transform(CtClass ctClass) throws Exception;
}
