package weaver.common;

import java.util.Set;

import javassist.CtClass;

/**
 * The interface for a weaver processor.
 * <p>
 * Processors can be defined by adding their canonical names into the <code>META-INF/weaver/processor</code> file.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Processor {

    /**
     * Initializes the processor with the weave environment.
     *
     * @param processingEnvironment Weave environment for processor.
     */
    void init(WeaveEnvironment processingEnvironment);

    /**
     * Weaver plugin will call this method for all defined processors, it gives all classes
     * <p>
     * Transformation can be confirmed by calling {@linkplain #writeClass(CtClass)}.
     *
     * @param candidateClasses All classes from project source set.
     * @throws Exception Throws if an error occurred during bytecode manipulation.
     */
    void transform(Set<? extends CtClass> candidateClasses) throws Exception;

    /**
     * Specifies which classes should be transformed, for example by returning {@link Scope#PROJECT}
     * all classes from source set will be passed to {@link #transform(Set)}.
     *
     * @return Returns the scope.
     */
    Scope getScope();

    /**
     * @param candidateClass Given {@link CtClass}.
     * @return Returns true, if writes successfully the <code>CtClass</code> in the proper directory
     * otherwise returns false.
     */
    boolean writeClass(CtClass candidateClass);
}
