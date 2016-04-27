package io.saeid.weaver.test.processor;

import javassist.CtClass;
import weaver.processor.WeaverProcessor;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Processor2 extends WeaverProcessor {
    @Override
    public void process(CtClass ctClass) {
        logger.quiet("Apply " + ctClass.toString());
    }

    @Override
    public boolean filter(CtClass ctClass) {
        logger.quiet("Filter on " + ctClass.toString());
        return false;
    }
}
