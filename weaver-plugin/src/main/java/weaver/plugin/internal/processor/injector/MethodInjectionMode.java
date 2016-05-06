package weaver.plugin.internal.processor.injector;

import javassist.CtMethod;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@Deprecated
enum MethodInjectionMode {
    AT_BEGINNING("$$AtBeginning"),
    AFTER_SUPER("$$AfterSuper"),
    BEFORE_SUPER("$$BeforeSuper"),
    BEFORE_RETURN("$$BeforeReturn");

    private String methodName;
    private String suffix;

    MethodInjectionMode(String suffix) {
        this.suffix = suffix;
    }

    public static MethodInjectionMode getType(CtMethod ctMethod) {
        String methodName = ctMethod.getName();
        for (MethodInjectionMode mode : values()) {
            if (methodName.endsWith(mode.suffix)) {
                return create(methodName, mode);
            }
        }
        return createDefault(methodName, BEFORE_RETURN);
    }

    private static MethodInjectionMode create(String methodName,
                                              MethodInjectionMode type) {
        int index = methodName.indexOf(type.suffix);
        type.methodName = methodName.substring(0, index);
        return type;
    }

    private static MethodInjectionMode createDefault(String methodName,
                                                     MethodInjectionMode type) {
        type.methodName = methodName;
        return type;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getSuffix() {
        return suffix;
    }
}
