package weaver.processor;

import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class WeaverProcessor {

    private Logger logger;

    public synchronized void init() {

    }

    public abstract void apply(CtClass ctClass);

    public abstract boolean filter(CtClass ctClass);
}
