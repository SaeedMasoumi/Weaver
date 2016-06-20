package io.saeid.weaver.sample.processor;

import java.util.Set;

import javassist.CtClass;
import weaver.processor.WeaverProcessor;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Processor1 extends WeaverProcessor {


    @Override
    public void transform(Set<? extends CtClass> candidateClasses) throws Exception {
        for (CtClass ctClass : candidateClasses) {
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
                    .inject();
            writeClass(ctClass);
        }
    }

    @Override
    public String getName() {
        return "Processor1";
    }
}
