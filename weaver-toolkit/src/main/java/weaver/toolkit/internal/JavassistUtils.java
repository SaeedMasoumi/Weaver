package weaver.toolkit.internal;

import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public final class JavassistUtils {

    public static boolean hasInterface(CtClass ctClass, CtClass givenInterface) throws
            NotFoundException {
        for (CtClass interfaceClass : ctClass.getInterfaces()) {
            if (givenInterface.getName().equals(interfaceClass.getName())) return true;
        }
        return false;
    }

    public static boolean isInterface(CtClass ctClass) {
        return Modifier.isInterface(ctClass.getModifiers());
    }

    public static boolean isAbstract(CtClass ctClass) {
        return Modifier.isAbstract(ctClass.getModifiers());
    }

    public static boolean hasField(CtClass ctClass, String fieldName) {
        for (CtField field : ctClass.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) return true;
        }
        return false;
    }
}
