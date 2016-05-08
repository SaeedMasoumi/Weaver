package weaver.common;

import javassist.CtClass;
import weaver.common.injection.ClassInjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Instrumentation {

    ClassInjector startWeaving(CtClass ctClass);
}

