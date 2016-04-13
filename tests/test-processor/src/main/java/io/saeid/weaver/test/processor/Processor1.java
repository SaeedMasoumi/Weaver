package io.saeid.weaver.test.processor;

import javassist.CtClass;
import weaver.processor.WeaverProcessor;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Processor1 extends WeaverProcessor {
    @Override
    public void apply(CtClass ctClass) {

    }

    @Override
    public boolean filter(CtClass ctClass) {
        return false;
    }
}
