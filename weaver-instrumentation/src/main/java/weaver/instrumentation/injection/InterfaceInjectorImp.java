package weaver.instrumentation.injection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import javassist.CtClass;
import weaver.common.injection.ClassInjector;
import weaver.common.injection.InterfaceInjector;

import static weaver.instrumentation.internal.JavassistUtils.hasInterface;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class InterfaceInjectorImp extends BaseInjector<ClassInjectorImp> implements
        InterfaceInjector<ClassInjector> {
    private ArrayList<String> qualifiedNames = new ArrayList<>();

    InterfaceInjectorImp(ClassInjectorImp classInjectorImp) {
        super(classInjectorImp);
    }

    @Override
    public InterfaceInjector implement(String fullQualifiedName) {
        if (!qualifiedNames.contains(fullQualifiedName)) {
            qualifiedNames.add(fullQualifiedName);
        }
        return this;
    }

    @Override
    public InterfaceInjector implement(Class<?> interfaceClass) {
        return implement(interfaceClass.getCanonicalName());
    }

    @Override
    public ClassInjector inject() throws Exception {
        //TODO check modifiers, e.g. different package names with private modifiers is not allowed.
        CtClass ctClass = getCtClass();
        for (String qualifiedName : qualifiedNames) {
            CtClass interfaceCtClass = getPool().get(qualifiedName);
            if (!Modifier.isInterface(interfaceCtClass.getModifiers())) {
                throw new Exception("InterfaceWeaver: Tried to weave " +
                        interfaceCtClass.getName() +
                        " but it's not an interface. ");
            }
            if (!hasInterface(ctClass, interfaceCtClass)) {
                ctClass.addInterface(interfaceCtClass);
            }
        }
        return parent;
    }
}
