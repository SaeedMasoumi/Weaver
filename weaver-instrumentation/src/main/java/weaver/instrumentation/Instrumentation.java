package weaver.instrumentation;

import javassist.ClassPool;
import javassist.CtClass;
import weaver.instrumentation.injection.ClassInjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Instrumentation {
    private ClassPool pool;

    public Instrumentation(ClassPool pool) {
        this.pool = pool;
    }

    public ClassInjector startWeaving(CtClass ctClass) {
        return new ClassInjector(ctClass, pool);
    }

}
