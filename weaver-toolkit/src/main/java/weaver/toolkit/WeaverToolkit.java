package weaver.toolkit;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class WeaverToolkit {
    private ClassPool pool;

    public WeaverToolkit(ClassPool pool) {
        this.pool = pool;
    }

    public ClassWeaver startWeaving(CtClass ctClass) {
        ctClass.defrost();
        return new ClassWeaver(ctClass, pool);
    }

}
