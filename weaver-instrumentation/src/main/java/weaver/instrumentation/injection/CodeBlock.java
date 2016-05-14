package weaver.instrumentation.injection;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class CodeBlock {
    private static final String ORIGINAL_CALL_EXP = "$_ = $proceed($$);";

    MethodInjectionMode where;
    String body = "";
    String aroundMethodCall;

    static CodeBlock atBeginningOrEnd(MethodInjectionMode mode, String statements) {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.setInjectionMode(mode);
        switch (mode) {
            case AT_THE_BEGINNING:
            case AT_THE_END:
                codeBlock.setBody(statements);
        }
        return codeBlock;
    }

    static CodeBlock afterOrBeforeStatements(MethodInjectionMode mode, String statements,
                                             String aroundMethod) {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.setInjectionMode(mode);
        switch (mode) {
            case AFTER_SUPER:
            case AFTER_A_CALL:
                codeBlock.setBody(unify(ORIGINAL_CALL_EXP, statements));
                break;
            case BEFORE_SUPER:
            case BEFORE_A_CALL:
                codeBlock.setBody(unify(statements, ORIGINAL_CALL_EXP));
                break;
        }
        if (aroundMethod != null) {
            codeBlock.setAroundMethodCall(aroundMethod);
        }
        return codeBlock;
    }

    static CodeBlock aroundMethod(MethodInjectionMode mode,
                                  String beforeCallStatements,
                                  String afterCallStatements, String aroundMethod) {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.setInjectionMode(mode);
        codeBlock.setAroundMethodCall(aroundMethod);
        switch (mode) {
            case AROUND_A_CALL:
                codeBlock.setBody(
                        unify(beforeCallStatements, ORIGINAL_CALL_EXP, afterCallStatements));
                break;
        }
        return codeBlock;
    }

    private static String unify(String... statements) {
        StringBuilder sb = new StringBuilder();
        for (String statement : statements) {
            sb.append(statement).append("\n");
        }
        return sb.toString();
    }


    void setAroundMethodCall(String aroundMethodCall) {
        this.aroundMethodCall = aroundMethodCall;
    }

    void setInjectionMode(MethodInjectionMode where) {
        this.where = where;
    }

    void setBody(String body) {
        this.body = body;
    }
}