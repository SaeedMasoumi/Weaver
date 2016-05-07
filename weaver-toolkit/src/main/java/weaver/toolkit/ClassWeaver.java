package weaver.toolkit;

import java.lang.ref.WeakReference;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class ClassWeaver implements ResourceBundle {

    private WeakReference<CtClass> ctClass;
    private WeakReference<ClassPool> pool;

    ClassWeaver(CtClass ctClass, ClassPool pool) {
        this.ctClass = new WeakReference<>(ctClass);
        this.pool = new WeakReference<>(pool);
    }

    public InterfaceWeaver insertInterface() {
        return new InterfaceWeaver(this);
    }

    public FieldWeaver insertField() {
        return new FieldWeaver(this);
    }

    public MethodWeaver insertMethod(String methodName) {
        return insertMethod(methodName, new String[0]);
    }

    public MethodWeaver insertMethod(String methodName, String... parameters) {
        return new MethodWeaver(this, methodName, parameters);
    }

    @Override
    public CtClass getCtClass() {
        return ctClass.get();
    }

    @Override
    public ClassPool getPool() {
        return pool.get();
    }
}
