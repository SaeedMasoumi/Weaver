package io.saeid.weaver.sample.processor;

import javassist.CtClass;
import weaver.processor.WeaverProcessor;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Processor2 extends WeaverProcessor {

    @Override
    public boolean filter(CtClass ctClass) {
        return true;
    }

    @Override
    public void transform(CtClass ctClass) throws Exception {
        logger.quiet("Start transforming " +
                ctClass.getSimpleName() +
                " with " +
                getClass().getSimpleName());
    }
}
