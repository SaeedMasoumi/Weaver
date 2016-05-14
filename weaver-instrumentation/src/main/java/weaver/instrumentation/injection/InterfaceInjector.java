package weaver.instrumentation.injection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import javassist.CtClass;

import static weaver.instrumentation.injection.InternalUtils.hasInterface;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class InterfaceInjector extends BaseInjector<ClassInjector> {
    private ArrayList<String> qualifiedNames = new ArrayList<>();

    InterfaceInjector(ClassInjector classInjector) {
        super(classInjector);
    }

    public InterfaceInjector implement(String fullQualifiedName) {
        if (!qualifiedNames.contains(fullQualifiedName)) {
            qualifiedNames.add(fullQualifiedName);
        }
        return this;
    }

    public InterfaceInjector implement(Class clazz) {
        return implement(clazz.getCanonicalName());
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
