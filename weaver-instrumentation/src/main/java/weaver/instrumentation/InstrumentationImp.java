package weaver.instrumentation;

import javassist.ClassPool;
import javassist.CtClass;
import weaver.common.Instrumentation;
import weaver.common.injection.ClassInjector;
import weaver.instrumentation.injection.ClassInjectorImp;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class InstrumentationImp implements Instrumentation {
    private ClassPool pool;

    public InstrumentationImp(ClassPool pool) {
        this.pool = pool;
    }

    @Override
    public ClassInjector startWeaving(CtClass ctClass) {
        return new ClassInjectorImp(ctClass, pool);
    }

}
