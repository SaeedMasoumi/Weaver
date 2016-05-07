package weaver.toolkit;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import static weaver.toolkit.internal.JavassistUtils.findMethod;
import static weaver.toolkit.internal.JavassistUtils.getAllMethods;
import static weaver.toolkit.internal.JavassistUtils.getDeclaredMethods;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class MethodWeaver extends BytecodeWeaver<ClassWeaver> {

    private String methodName = "weaverMethod";

    private String[] parameters = null;
    private MethodNotExistsState methodNotExistsState = null;

    MethodWeaver(ClassWeaver classWeaver, String methodName, String[] parameters) {
        super(classWeaver);
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public MethodExistsState ifMethodExists() {
        return new MethodExistsState(this);
    }

    public MethodExistsButNotOverrideState ifMethodExistsButNotOverride() {
        return new MethodExistsButNotOverrideState(this);
    }

    public MethodNotExistsState createMethodIfNotExists() {
        methodNotExistsState = new MethodNotExistsState(this);
        return methodNotExistsState;
    }

    @Override
    protected void weaving() throws Exception {
        CtMethod[] allMethods = getAllMethods(getCtClass());
        CtMethod[] declaredMethods = getDeclaredMethods(getCtClass());
        CtMethod methodInParent = findMethod(methodName, parameters, allMethods);
        CtMethod methodInClass = findMethod(methodName, parameters, declaredMethods);
        //method not exists
        if (methodInParent == null && methodInClass == null && methodNotExistsState != null) {
            CtMethod method = CtNewMethod.make(methodNotExistsState.buildMethod(), getCtClass());
            getCtClass().addMethod(method);
        }
        //if method exists but not override
        //if method exists
    }

    private String[] getParameters() {
        return parameters;
    }

    private String getMethodName() {
        return methodName;
    }

    public static class MethodExistsState extends BytecodeWeaver<MethodWeaver> {

        MethodExistsState(MethodWeaver methodWeaver) {
            super(methodWeaver);
        }

        @Override
        protected void weaving() throws Exception {

        }
    }

    public static class MethodNotExistsState extends BytecodeWeaver<MethodWeaver> {

        private int modifiers = 0;
        private String returnType = "void";
        private String body = "";
        private String[] parametersName = null;

        MethodNotExistsState(MethodWeaver methodWeaver) {
            super(methodWeaver);
        }

        public MethodNotExistsState modifiers(int... modifiers) {
            for (int m : modifiers) this.modifiers |= m;
            return this;
        }

        public MethodNotExistsState returnType(String qualifiedName) {
            this.returnType = qualifiedName;
            return this;
        }

        public MethodNotExistsState parametersName(String... params) {
            parametersName = params;
            return this;
        }

        public MethodNotExistsState body(String body) {
            this.body = body;
            return this;
        }

        @Override
        protected void weaving() throws Exception {
        }

        private String buildMethod() throws CannotCompileException {
            StringBuilder sb = new StringBuilder();
            //"modifiers"
            sb.append(Modifier.toString(modifiers)).append(" ");
            //"modifiers" "returnType" "methodName"
            sb.append(returnType).append(" ").append(parent.getMethodName())
                    .append("(");
            //"modifiers" "returnType" "methodName"(P p1,P p2,...,P pn)
            String[] params = parent.getParameters();
            if (params == null) {
                sb.append(")");
            } else {
                if (parametersName == null) {
                    parametersName = new String[params.length];
                    for (int i = 0; i < params.length; i++) {
                        parametersName[i] = "p" + (i + 1);
                    }
                }
                for (int i = 0; i < params.length; i++) {
                    sb.append(params[i]).append(" ").append(parametersName[i]);
                    if (i != params.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
            //"modifiers" "returnType" "methodName"(P p1,P p2,...,P pn){body}
            sb.append(body);
            return sb.toString();
        }
    }

    public static class MethodExistsButNotOverrideState extends BytecodeWeaver<MethodWeaver> {

        MethodExistsButNotOverrideState(MethodWeaver methodWeaver) {
            super(methodWeaver);
        }

        @Override
        protected void weaving() throws Exception {

        }
    }
}
