package weaver.instrumentation.injection;

import java.lang.ref.WeakReference;

import javassist.ClassPool;
import javassist.CtClass;
import weaver.common.injection.ClassInjector;
import weaver.common.injection.FieldInjector;
import weaver.common.injection.InterfaceInjector;
import weaver.common.injection.MethodInjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class ClassInjectorImp implements ClassInjector, ResourceBundle {

    private WeakReference<CtClass> ctClass;
    private WeakReference<ClassPool> pool;

    public ClassInjectorImp(CtClass ctClass, ClassPool pool) {
        this.ctClass = new WeakReference<>(ctClass);
        this.pool = new WeakReference<>(pool);
    }

    @Override
    public InterfaceInjector insertInterface() {
        return new InterfaceInjectorImp(this);
    }

    @Override
    public FieldInjector insertField(Class type, String name) {
        return insertField(type.getCanonicalName(), name);
    }

    @Override
    public FieldInjector insertField(String type, String name) {
        return new FieldInjectorImp(this, type, name);
    }

    @Override
    public MethodInjector insertMethod(String methodName) {
        return insertMethod(methodName, new String[0]);
    }

    @Override
    public MethodInjector insertMethod(String methodName, Class... parameters) {
        String[] parametersName = new String[parameters.length];
        for (int i = 0; i < parametersName.length; i++) {
            parametersName[i] = parameters[i].getCanonicalName();
        }
        return insertMethod(methodName, parametersName);
    }

    @Override
    public MethodInjector insertMethod(String methodName, String... parameters) {
        return new MethodInjectorImp(this, methodName, parameters);
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
