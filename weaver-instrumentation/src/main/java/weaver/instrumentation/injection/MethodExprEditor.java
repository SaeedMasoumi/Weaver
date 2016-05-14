package weaver.instrumentation.injection;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static weaver.instrumentation.injection.MethodInjectionMode.AFTER_A_CALL;
import static weaver.instrumentation.injection.MethodInjectionMode.AFTER_SUPER;


/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class MethodExprEditor extends ExprEditor {
    private static final String DEFAULT_EXPR = "$_ = $proceed($$);";

    private MethodInjector.Statement statement;

    MethodExprEditor(MethodInjector.Statement statement) {
        this.statement = statement;
    }

    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        StringBuilder rb = new StringBuilder();
        switch (statement.injectionMode) {
            case AFTER_SUPER:
            case BEFORE_SUPER:
                if (!m.isSuper()) return;
                injectBodyBeforeOrAfterSuper(rb);
                break;
            case AFTER_A_CALL:
            case BEFORE_A_CALL:
                if (!m.getMethodName().equals(statement.methodCall)) return;
                injectBeforeOrAfterACall(rb);
        }
        m.replace(rb.toString());
        super.edit(m);
    }

    private void injectBodyBeforeOrAfterSuper(StringBuilder rb) {
        if (statement.injectionMode.equals(AFTER_SUPER)) {
            rb.append(DEFAULT_EXPR).append("\n").append(statement.body);
        } else {
            rb.append(statement.body).append("\n").append(DEFAULT_EXPR);
        }
    }

    private void injectBeforeOrAfterACall(StringBuilder rb) {
        if (statement.injectionMode.equals(AFTER_A_CALL)) {
            rb.append(DEFAULT_EXPR).append("\n").append(statement.body);
        } else {
            rb.append(statement.body).append("\n").append(DEFAULT_EXPR);
        }
    }
}
