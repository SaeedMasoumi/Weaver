package weaver.processor;

import javassist.CtClass;
import weaver.common.Instrumentation;
import weaver.common.Logger;
import weaver.common.Processor;
import weaver.common.WeaveEnvironment;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class WeaverProcessor implements Processor {

    protected Logger logger;
    protected Instrumentation instrumentation;

    public synchronized void init(WeaveEnvironment env) {
        logger = env.getLogger();
        instrumentation = env.getInstrumentation();
    }

    public abstract void transform(CtClass ctClass) throws Exception;

    public abstract boolean filter(CtClass ctClass);
}
