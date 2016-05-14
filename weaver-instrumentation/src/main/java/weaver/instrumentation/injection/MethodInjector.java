package weaver.instrumentation.injection;

import java.util.ArrayList;

import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import static weaver.instrumentation.injection.InternalUtils.findMethod;
import static weaver.instrumentation.injection.InternalUtils.getAllMethods;
import static weaver.instrumentation.injection.InternalUtils.getDeclaredMethods;
import static weaver.instrumentation.injection.InternalUtils.getModifiers;
import static weaver.instrumentation.injection.MethodInjectionMode.AFTER_A_CALL;
import static weaver.instrumentation.injection.MethodInjectionMode.AFTER_SUPER;
import static weaver.instrumentation.injection.MethodInjectionMode.AROUND_A_CALL;
import static weaver.instrumentation.injection.MethodInjectionMode.AT_THE_BEGINNING;
import static weaver.instrumentation.injection.MethodInjectionMode.AT_THE_END;
import static weaver.instrumentation.injection.MethodInjectionMode.BEFORE_A_CALL;
import static weaver.instrumentation.injection.MethodInjectionMode.BEFORE_SUPER;

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
            for (CodeBlock codeBlock : existsMode.getCodeBlocks()) {
                switch (codeBlock.where) {
                    case AT_THE_BEGINNING:
                        methodInClass.insertBefore(codeBlock.body);
                        break;
                    case BEFORE_SUPER:
                    case AFTER_SUPER:
                    case AFTER_A_CALL:
                    case BEFORE_A_CALL:
                    case AROUND_A_CALL:
                        MethodExprEditor editor = new MethodExprEditor(codeBlock);
                        methodInClass.instrument(editor);
                        break;
                    case AT_THE_END:
                        methodInClass.insertAfter(codeBlock.body);
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
        private ArrayList<CodeBlock> codeBlocks = new ArrayList<>();

        MethodInjectorExistsMode(MethodInjector methodInjector) {
            super(methodInjector);
        }

        public MethodInjectorExistsMode atTheBeginning(String statements) {
            this.codeBlocks.add(CodeBlock.atBeginningOrEnd(AT_THE_BEGINNING, statements));
            return this;
        }

        public MethodInjectorExistsMode atTheEnd(String statements) {
            this.codeBlocks.add(CodeBlock.atBeginningOrEnd(AT_THE_END, statements));
            return this;
        }

        public MethodInjectorExistsMode afterSuper(String statements) {
            this.codeBlocks.add(CodeBlock.afterOrBeforeStatements(AFTER_SUPER, statements, null));
            return this;
        }

        public MethodInjectorExistsMode beforeSuper(String statements) {
            this.codeBlocks.add(CodeBlock.afterOrBeforeStatements(BEFORE_SUPER, statements, null));
            return this;
        }

        public MethodInjectorExistsMode afterACallTo(String call, String statements) {
            this.codeBlocks.add(CodeBlock.afterOrBeforeStatements(AFTER_A_CALL, statements, call));
            return this;
        }

        public MethodInjectorExistsMode beforeACallTo(String call, String statements) {
            this.codeBlocks.add(CodeBlock.afterOrBeforeStatements(BEFORE_A_CALL, statements, call));
            return this;
        }

        public MethodInjectorExistsMode aroundACallTo(String call, String beforeCallCode,
                                                      String afterCallCode) {
            this.codeBlocks.add(
                    CodeBlock.aroundMethod(AROUND_A_CALL, beforeCallCode, afterCallCode,
                            call));
            return this;
        }

        @Override
        public MethodInjector inject() throws Exception {
            return parent;
        }

        ArrayList<CodeBlock> getCodeBlocks() {
            return codeBlocks;
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
