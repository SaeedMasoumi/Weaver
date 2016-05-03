package weaver.plugin.internal.processor.injector;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class MethodExprEditor extends ExprEditor {
    private static final String DEFAULT_EXPR = "$_ = $proceed($$);";

    private MethodInjectionMode injectionMode;
    private String methodCall;

    MethodExprEditor(MethodInjectionMode injectionMode, String methodCall) {
        this.injectionMode = injectionMode;
        this.methodCall = methodCall;
    }

    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        StringBuilder rb = new StringBuilder();
        switch (injectionMode) {
            case AFTER_SUPER:
                if (!m.isSuper()) return;
                rb.append(DEFAULT_EXPR).append("\n").append(methodCall);
                break;
            case BEFORE_SUPER:
                if (!m.isSuper()) return;
                rb.append(methodCall).append("\n").append(DEFAULT_EXPR);
                break;
        }
        m.replace(rb.toString());
        super.edit(m);
    }
}
