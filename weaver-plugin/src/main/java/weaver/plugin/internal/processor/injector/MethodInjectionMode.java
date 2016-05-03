package weaver.plugin.internal.processor.injector;

import javassist.CtMethod;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
enum MethodInjectionMode {
    AT_BEGINNING,
    AFTER,
    BEFORE,
    AFTER_SUPER,
    BEFORE_SUPER,
    BEFORE_RETURN;

    private String methodName;

    public static MethodInjectionMode getType(CtMethod method) {
        String methodName = method.getName();
        if (methodName.endsWith("$$After")) {
            return create(methodName, "$$After", AFTER);
        } else if (methodName.endsWith("$$Before")) {
            return create(methodName, "$$Before", BEFORE);
        } else if (methodName.endsWith("$$AfterSuper")) {
            return create(methodName, "$$AfterSuper", AFTER_SUPER);
        } else if (methodName.endsWith("$$BeforeSuper")) {
            return create(methodName, "$$BeforeSuper", BEFORE_SUPER);
        } else if (methodName.endsWith("$$AtBeginning")) {
            return create(methodName, "$$AtBeginning", AT_BEGINNING);
        } else if (methodName.endsWith("$$BeforeReturn")) {
            return create(methodName, "$$BeforeReturn", BEFORE_RETURN);
        }
        return create(methodName, BEFORE_RETURN);
    }

    private static MethodInjectionMode create(String methodName, String suffix,
                                              MethodInjectionMode type) {
        int index = methodName.indexOf(suffix);
        type.setMethodName(methodName.substring(0, index));
        return type;
    }

    private static MethodInjectionMode create(String methodName,
                                              MethodInjectionMode type) {
        type.setMethodName(methodName);
        return type;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
