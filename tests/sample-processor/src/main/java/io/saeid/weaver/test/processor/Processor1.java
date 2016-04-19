package io.saeid.weaver.test.processor;

import javassist.CtClass;
import weaver.processor.WeaverProcessor;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Processor1 extends WeaverProcessor {

    @Override
    public void apply(CtClass ctClass) {
        logger.quiet("Apply " + ctClass.getName() + "\n");
    }

    @Override
    public boolean filter(CtClass ctClass) {
        logger.quiet("Filter on " + ctClass.getName() + "\n");
        return false;
    }
}
