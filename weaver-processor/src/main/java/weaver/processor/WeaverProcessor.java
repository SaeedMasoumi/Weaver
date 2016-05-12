package weaver.processor;

import javassist.CtClass;
import weaver.common.Logger;
import weaver.common.Processor;
import weaver.common.WeaveEnvironment;
import weaver.instrumentation.Instrumentation;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class WeaverProcessor implements Processor {

    protected WeaveEnvironment weaveEnvironment;
    protected Logger logger;
    protected Instrumentation instrumentation;

    public synchronized void init(WeaveEnvironment env) {
        weaveEnvironment = env;
        logger = env.getLogger();
        instrumentation = new Instrumentation(env.getClassPool());
    }

    public abstract boolean filter(CtClass ctClass);

    public abstract void transform(CtClass ctClass) throws Exception;
}
