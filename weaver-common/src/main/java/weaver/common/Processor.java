package weaver.common;

import java.util.Set;

import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Processor {

    void init(WeaveEnvironment processingEnvironment);

    void transform(Set<? extends CtClass> candidateClasses) throws Exception;

    boolean writeClass(CtClass candidateClass);
}
