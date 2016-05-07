package weaver.toolkit;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static weaver.toolkit.MethodInjection.AFTER_SUPER;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class MethodExprEditor extends ExprEditor {
    private static final String DEFAULT_EXPR = "$_ = $proceed($$);";

    private MethodInjection methodInjection;

    MethodExprEditor(MethodInjection injectionMode) {
        this.methodInjection = injectionMode;
    }

    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        StringBuilder rb = new StringBuilder();
        switch (methodInjection) {
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
        if (methodInjection.equals(AFTER_SUPER)) {
            rb.append(DEFAULT_EXPR).append("\n").append(methodInjection.getBody());
        } else {
            rb.append(methodInjection.getBody()).append("\n").append(DEFAULT_EXPR);
        }
    }
}
