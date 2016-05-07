package weaver.toolkit.internal;

import java.util.Arrays;
import java.util.List;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
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

    public static boolean hasField(CtClass ctClass, String fieldName) {
        for (CtField field : ctClass.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) return true;
        }
        return false;
    }

    public static CtMethod[] getAllMethods(CtClass ctClass) {
        return ctClass.getMethods();
    }

    public static CtMethod[] getDeclaredMethods(CtClass ctClass) {
        return ctClass.getDeclaredMethods();
    }

    public static CtMethod findMethod(String methodName, String[] parameters,
                                      CtMethod[] allMethods) throws NotFoundException {
        List<String> paramsList = Arrays.asList(parameters);
        for (CtMethod method : allMethods) {
            if (method.getName().equals(methodName) && sameSignature(paramsList, method)) {
                return method;
            }
        }
        return null;
    }

    public static boolean sameSignature(List<String> parameters, CtMethod method)
            throws NotFoundException {
        CtClass[] methodParameters = method.getParameterTypes();
        if (methodParameters.length == 0 && parameters.size() == 0) return true;
        if (methodParameters.length != 0 && parameters.size() == 0) return false;
        if (methodParameters.length == 0 && parameters.size() != 0) return false;
        for (CtClass clazz : method.getParameterTypes()) {
            if (!parameters.contains(clazz.getName())) return false;
        }
        return true;
    }
}
