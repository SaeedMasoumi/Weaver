package weaver.instrumentation.injection;

import java.lang.ref.WeakReference;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class ClassInjector implements ResourceBundle {

    private WeakReference<CtClass> ctClass;
    private WeakReference<ClassPool> pool;

    public ClassInjector(CtClass ctClass, ClassPool pool) {
        this.ctClass = new WeakReference<>(ctClass);
        this.pool = new WeakReference<>(pool);
    }

    public InterfaceInjector insertInterface() {
        return new InterfaceInjector(this);
    }

    public FieldInjector insertField(Class type, String name) {
        return insertField(type.getCanonicalName(), name);
    }

    public FieldInjector insertField(String type, String name) {
        return new FieldInjector(this, type, name);
    }

    public MethodInjector insertMethod(String methodName) {
        return insertMethod(methodName, new String[0]);
    }

    public MethodInjector insertMethod(String methodName, Class... parameters) {
        String[] parametersName = new String[parameters.length];
        for (int i = 0; i < parametersName.length; i++) {
            parametersName[i] = parameters[i].getCanonicalName();
        }
        return insertMethod(methodName, parametersName);
    }

    public MethodInjector insertMethod(String methodName, String... parameters) {
        return new MethodInjector(this, methodName, parameters);
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
