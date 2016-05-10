package io.saeid.weaver.sample.processor;

import javassist.CtClass;
import weaver.processor.WeaverProcessor;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Processor1 extends WeaverProcessor {

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

        instrumentation.startWeaving(ctClass)
                .insertInterface()
                .implement(Runnable.class)
                .inject()
                .insertMethod("run")
                .ifExistsButNotOverride()
                .override("{" +
                        "System.out.println(\"run\");" +
                        "}")
                .inject()
                .inject()
        ;
    }
}
