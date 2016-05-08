package weaver.instrumentation.injection;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static weaver.instrumentation.injection.MethodInjectionMode.AFTER_SUPER;


/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class MethodExprEditor extends ExprEditor {
    private static final String DEFAULT_EXPR = "$_ = $proceed($$);";

    private MethodInjectorImp.Statement statement;

    MethodExprEditor(MethodInjectorImp.Statement statement) {
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
}
