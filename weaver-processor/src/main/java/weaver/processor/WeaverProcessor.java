package weaver.processor;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.CtClass;
import weaver.common.Logger;
import weaver.common.Processor;
import weaver.common.Scope;
import weaver.common.WeaveEnvironment;
import weaver.instrumentation.Instrumentation;

/**
 * A concrete implementation of {@link Processor}. It also provides {@link Instrumentation} utilities.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class WeaverProcessor implements Processor {

    protected WeaveEnvironment weaveEnvironment;
    protected Logger logger;
    protected Instrumentation instrumentation;

    private String outputPath;

    /**
     * {@inheritDoc}
     */
    public synchronized void init(WeaveEnvironment env) {
        weaveEnvironment = env;
        logger = env.getLogger();
        instrumentation = new Instrumentation(env.getClassPool());
        outputPath = weaveEnvironment.getOutputDir().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scope getScope() {
        return Scope.PROJECT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean writeClass(CtClass candidateClass) {
        try {
            candidateClass.writeFile(outputPath);
            candidateClass.defrost();//allows other processors to modify this class
            logger.info("Writing " + candidateClass.getSimpleName() + " into " + outputPath);
        } catch (CannotCompileException e) {
            logger.info(e.getMessage());
            return false;
        } catch (IOException e) {
            logger.info(e.getMessage());
            return false;
        }
        return true;
    }
}
