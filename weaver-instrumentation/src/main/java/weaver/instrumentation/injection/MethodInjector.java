package weaver.instrumentation.injection;

import java.util.ArrayList;

import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import static weaver.instrumentation.injection.MethodInjectionMode.AFTER_A_CALL;
import static weaver.instrumentation.injection.MethodInjectionMode.AFTER_SUPER;
import static weaver.instrumentation.injection.MethodInjectionMode.AT_THE_BEGINNING;
import static weaver.instrumentation.injection.MethodInjectionMode.AT_THE_END;
import static weaver.instrumentation.injection.MethodInjectionMode.BEFORE_A_CALL;
import static weaver.instrumentation.injection.MethodInjectionMode.BEFORE_SUPER;
import static weaver.instrumentation.internal.JavassistUtils.findMethod;
import static weaver.instrumentation.internal.JavassistUtils.getAllMethods;
import static weaver.instrumentation.internal.JavassistUtils.getDeclaredMethods;
import static weaver.instrumentation.internal.JavassistUtils.getModifiers;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class MethodInjector extends BaseInjector<ClassInjector> {

    private String methodName;
    private String[] parameters = null;

    private MethodInjectorExistsMode existsMode = null;
    private MethodInjectorOverrideMode overrideMode = null;
    private MethodInjectorNotExistsMode notExistsMode = null;

    MethodInjector(ClassInjector classInjector, String methodName, String[] parameters) {
        super(classInjector);
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public MethodInjectorExistsMode ifExists() {
        existsMode = new MethodInjectorExistsMode(this);
        return existsMode;
    }

    public MethodInjectorOverrideMode ifExistsButNotOverride() {
        overrideMode = new MethodInjectorOverrideMode(this);
        return overrideMode;
    }

    public MethodInjectorNotExistsMode createIfNotExists() {
        notExistsMode = new MethodInjectorNotExistsMode(this);
        return notExistsMode;
    }

    @Override
    public ClassInjector inject() throws Exception {
        CtMethod[] allMethods = getAllMethods(getCtClass());
        CtMethod[] declaredMethods = getDeclaredMethods(getCtClass());
        CtMethod methodInParent = findMethod(methodName, parameters, allMethods);
        CtMethod methodInClass = findMethod(methodName, parameters, declaredMethods);
        //method not exists
        CtMethod method = null;
        if (methodInParent == null && methodInClass == null && notExistsMode != null) {
            method = CtNewMethod.make(notExistsMode.getFullMethod(), getCtClass());
        }
        //if method exists but not override
        else if (methodInParent != null && methodInClass == null && overrideMode != null) {
            method = CtNewMethod.make(methodInParent.getReturnType(), methodInParent.getName(),
                    methodInParent.getParameterTypes(), methodInParent.getExceptionTypes(),
                    overrideMode.getFullMethod(),
                    getCtClass());
            method.setModifiers(methodInParent.getModifiers() & ~Modifier.ABSTRACT);
        } else if (methodInClass != null && existsMode != null) {
            for (Statement statement : existsMode.getStatements()) {
                switch (statement.injectionMode) {
                    case AT_THE_BEGINNING:
                        methodInClass.insertBefore(statement.body);
                        break;
                    case BEFORE_SUPER:
                    case AFTER_SUPER:
                        MethodExprEditor editor = new MethodExprEditor(statement);
                        methodInClass.instrument(editor);
                        break;
                    case AT_THE_END:
                        methodInClass.insertAfter(statement.body);
                        break;
                }
            }
        }
        if (method != null) getCtClass().addMethod(method);

        return parent;
    }

    private String getMethodName() {
        return methodName;
    }

    private String[] getParameters() {
        return parameters;
    }

    public static class MethodInjectorExistsMode extends BaseInjector<MethodInjector> {
        private ArrayList<Statement> statements = new ArrayList<>();

        MethodInjectorExistsMode(MethodInjector methodInjector) {
            super(methodInjector);
        }

        public MethodInjectorExistsMode atTheBeginning(String statements) {
            this.statements.add(new Statement(AT_THE_BEGINNING, statements));
            return this;
        }

        public MethodInjectorExistsMode atTheEnd(String statements) {
            this.statements.add(new Statement(AT_THE_END, statements));
            return this;
        }

        public MethodInjectorExistsMode afterSuper(String statements) {
            this.statements.add(new Statement(AFTER_SUPER, statements));
            return this;
        }

        public MethodInjectorExistsMode beforeSuper(String statements) {
            this.statements.add(new Statement(BEFORE_SUPER, statements));
            return this;
        }

        public MethodInjectorExistsMode afterACallTo(String call, String statements) {
            this.statements.add(new Statement(AFTER_A_CALL, statements, call));
            return this;
        }

        public MethodInjectorExistsMode beforeACallTo(String call, String statements) {
            this.statements.add(new Statement(BEFORE_A_CALL, statements, call));
            return this;
        }

        @Override
        public MethodInjector inject() throws Exception {
            return parent;
        }

        ArrayList<Statement> getStatements() {
            return statements;
        }
    }

    public static class MethodInjectorOverrideMode extends BaseInjector<MethodInjector> {

        private String fullMethod;

        MethodInjectorOverrideMode(MethodInjector methodInjector) {
            super(methodInjector);
        }

        public MethodInjectorOverrideMode override(String fullMethodBody) {
            this.fullMethod = fullMethodBody;
            return this;
        }

        @Override
        public MethodInjector inject() throws Exception {
            return parent;
        }

        String getFullMethod() {
            return fullMethod;
        }
    }

    static class Statement {
        MethodInjectionMode injectionMode;
        String body;
        String around;

        Statement(MethodInjectionMode injectionMode, String body) {
            this.injectionMode = injectionMode;
            this.body = body;
        }

        Statement(MethodInjectionMode injectionMode, String body, String around) {
            this.injectionMode = injectionMode;
            this.body = body;
            this.around = around;
        }
    }

    public static class MethodInjectorNotExistsMode extends BaseInjector<MethodInjector> {

        private int modifiers = 0;
        private String returnType = "void";
        private String body = "";
        private String[] parametersName = new String[0];
        private String fullMethod = "";

        MethodInjectorNotExistsMode(MethodInjector methodInjector) {
            super(methodInjector);
        }

        public MethodInjectorNotExistsMode returns(Class clazz) {
            return returns(clazz.getCanonicalName());
        }

        public MethodInjectorNotExistsMode returns(String fullQualifiedName) {
            this.returnType = fullQualifiedName;
            return this;
        }

        public MethodInjectorNotExistsMode addModifiers(int... modifiers) {
            this.modifiers = getModifiers(this.modifiers, modifiers);
            return this;
        }

        public MethodInjectorNotExistsMode setParametersName(String... parametersName) {
            this.parametersName = parametersName;
            return this;
        }

        public MethodInjectorNotExistsMode withBody(String body) {
            this.body = body;
            return this;
        }

        @Override
        public MethodInjector inject() throws Exception {
            StringBuilder sb = new StringBuilder();
            //"modifiers"
            sb.append(Modifier.toString(modifiers)).append(" ");
            //"modifiers" "returnType" "methodName"
            sb.append(returnType).append(" ").append(parent.getMethodName())
                    .append("(");
            //"modifiers" "returnType" "methodName"(P p1,P p2,...,P pn)
            String[] params = parent.getParameters();
            if (params == null || params.length == 0) {
                sb.append(")");
            } else {
                if (parametersName == null || parametersName.length == 0) {
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
            fullMethod = sb.toString();
            return parent;
        }

        String getFullMethod() {
            return fullMethod;
        }
    }
}
