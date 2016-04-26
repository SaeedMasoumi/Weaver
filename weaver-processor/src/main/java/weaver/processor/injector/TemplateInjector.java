package weaver.processor.injector;

import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface TemplateInjector {

    void inject(Class templateClass, CtClass sourceClass);
}
