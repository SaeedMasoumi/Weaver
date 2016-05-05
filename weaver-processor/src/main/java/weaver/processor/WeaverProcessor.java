package weaver.processor;

import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class WeaverProcessor {

    protected Logger logger;
    protected TemplateInjector templateInjector;
    protected Toolkit toolkit;

    public synchronized void init(ProcessingEnvironment env) {
        logger = env.getLogger();
        templateInjector = env.getTemplateInjector();
        toolkit = env.getToolkit();
    }

    public abstract void process(CtClass ctClass) throws Exception;

    public abstract boolean filter(CtClass ctClass);
}
