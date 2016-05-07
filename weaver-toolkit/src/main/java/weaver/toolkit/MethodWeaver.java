package weaver.toolkit;

import java.util.ArrayList;

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
    private MethodExistsButNotOverrideState overrideState = null;
    private MethodExistsState existsState = null;

    MethodWeaver(ClassWeaver classWeaver, String methodName, String[] parameters) {
        super(classWeaver);
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public MethodExistsState ifExists() {
        existsState = new MethodExistsState(this);
        return existsState;
    }

    public MethodExistsButNotOverrideState ifExistsButNotOverride() {
        overrideState = new MethodExistsButNotOverrideState(this);
        return overrideState;
    }

    public MethodNotExistsState createIfNotExists() {
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
        CtMethod method = null;
        if (methodInParent == null && methodInClass == null && methodNotExistsState != null) {
            method = CtNewMethod.make(methodNotExistsState.buildMethod(), getCtClass());
        }
        //if method exists but not override
        else if (methodInParent != null && methodInClass == null && overrideState != null) {
            method = CtNewMethod.make(methodInParent.getReturnType(), methodInParent.getName(),
                    methodInParent.getParameterTypes(), methodInParent.getExceptionTypes(),
                    overrideState.getBody(),
                    getCtClass());
            method.setModifiers(methodInParent.getModifiers() & ~Modifier.ABSTRACT);
        }
        //if method exists
        else if (methodInClass != null && existsState != null) {
            for (MethodInjection methodInjection : existsState.getMethodInjections()) {
                switch (methodInjection) {
                    case AT_THE_BEGINNING:
                        methodInClass.insertBefore(methodInjection.getBody());
                        break;
                    case BEFORE_SUPER:
                    case AFTER_SUPER:
                        MethodExprEditor editor = new MethodExprEditor(methodInjection);
                        methodInClass.instrument(editor);
                        break;
                    case AT_THE_END:
                        methodInClass.insertAfter(methodInjection.getBody());
                        break;
                }
            }
        }
        if (method != null) getCtClass().addMethod(method);
    }

    private String[] getParameters() {
        return parameters;
    }

    private String getMethodName() {
        return methodName;
    }

    public static class MethodExistsState extends BytecodeWeaver<MethodWeaver> {

        private ArrayList<MethodInjection> methodInjections = new ArrayList<>();

        MethodExistsState(MethodWeaver methodWeaver) {
            super(methodWeaver);
        }

        public MethodInsertion atTheBeginning() {
            return new MethodInsertion(this, MethodInjection.AT_THE_BEGINNING);
        }

        public MethodInsertion atTheEnd() {
            return new MethodInsertion(this, MethodInjection.AT_THE_END);
        }

        public MethodInsertion afterSuper() {
            return new MethodInsertion(this, MethodInjection.AFTER_SUPER);
        }

        public MethodInsertion beforeSuper() {
            return new MethodInsertion(this, MethodInjection.BEFORE_SUPER);
        }

        //        public MethodInsertion afterACallTo() {
        //
        //        }
        //
        //        public MethodInsertion beforeACallTo() {
        //
        //        }

        @Override
        protected void weaving() throws Exception {

        }

        void addNewCall(MethodInjection type) {
            methodInjections.add(type);
        }

        public ArrayList<MethodInjection> getMethodInjections() {
            return methodInjections;
        }
    }

    public static class MethodInsertion extends BytecodeWeaver<MethodExistsState> {

        private MethodInjection type;

        MethodInsertion(MethodExistsState methodExistsState, MethodInjection type) {
            super(methodExistsState);
            this.type = type;
        }

        public MethodExistsState withBody(String body) throws Exception {
            type.setBody(body);
            return done();
        }

        @Override
        protected void weaving() throws Exception {
            parent.addNewCall(type);
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

        private String body = "";

        MethodExistsButNotOverrideState(MethodWeaver methodWeaver) {
            super(methodWeaver);
        }

        public MethodExistsButNotOverrideState override(String body) {
            this.body = body;
            return this;
        }

        @Override
        protected void weaving() throws Exception {

        }

        private String getBody() {
            return body;
        }
    }
}
