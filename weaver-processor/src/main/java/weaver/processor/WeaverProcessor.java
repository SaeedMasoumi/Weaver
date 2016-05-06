package weaver.processor;

import javassist.CtClass;
import weaver.toolkit.WeaverToolkit;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class WeaverProcessor {

    protected Logger logger;
    protected WeaverToolkit weaverToolkit;

    public synchronized void init(ProcessingEnvironment env) {
        logger = env.getLogger();
        weaverToolkit = env.getWeaverToolkit();
    }

    public abstract void process(CtClass ctClass) throws Exception;

    public abstract boolean filter(CtClass ctClass);
}
