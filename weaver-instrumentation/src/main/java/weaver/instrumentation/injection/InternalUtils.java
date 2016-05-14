package weaver.instrumentation.injection;

import java.util.Arrays;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public final class InternalUtils {

    static boolean hasInterface(CtClass ctClass, CtClass givenInterface) throws
            NotFoundException {
        for (CtClass interfaceClass : ctClass.getInterfaces()) {
            if (givenInterface.getName().equals(interfaceClass.getName())) return true;
        }
        return false;
    }

    static CtMethod[] getAllMethods(CtClass ctClass) {
        return ctClass.getMethods();
    }

    static CtMethod[] getDeclaredMethods(CtClass ctClass) {
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

    static boolean sameSignature(List<String> parameters, CtMethod method)
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

    static int getModifiers(int defaultModifier, int... newModifiers) {
        for (int m : newModifiers) defaultModifier |= m;
        return defaultModifier;
    }

    static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
