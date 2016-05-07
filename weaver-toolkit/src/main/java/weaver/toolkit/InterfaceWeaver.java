package weaver.toolkit;

import java.util.ArrayList;

import javassist.CtClass;
import weaver.toolkit.exception.InterfaceConflictException;

import static weaver.toolkit.internal.JavassistUtils.hasInterface;
import static weaver.toolkit.internal.JavassistUtils.isInterface;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class InterfaceWeaver extends BytecodeWeaver<ClassWeaver> {

    private ArrayList<String> qualifiedNames = new ArrayList<>();

    InterfaceWeaver(ClassWeaver classWeaver) {
        super(classWeaver);
    }

    public InterfaceWeaver name(String qualifiedName) {
        if (!qualifiedNames.contains(qualifiedName)) {
            qualifiedNames.add(qualifiedName);
        }
        return this;
    }

    @Override
    protected void weaving() throws Exception {
        //TODO check modifiers, e.g. different package names with private modifiers is not allowed.
        CtClass ctClass = getCtClass();
        for (String qualifiedName : qualifiedNames) {
            CtClass interfaceCtClass = getPool().get(qualifiedName);
            if (!isInterface(interfaceCtClass)) {
                throw new InterfaceConflictException("InterfaceWeaver: Tried to weave " +
                        interfaceCtClass.getName() +
                        " but it's not an interface. ");
            }
            if (!hasInterface(ctClass, interfaceCtClass)) {
                ctClass.addInterface(interfaceCtClass);
            }
        }
    }

}
