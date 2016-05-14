package weaver.instrumentation.injection;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;


/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class MethodExprEditor extends ExprEditor {

    private CodeBlock codeBlock;

    MethodExprEditor(CodeBlock codeBlock) {
        this.codeBlock = codeBlock;
    }

    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        StringBuilder rb = new StringBuilder();
        if (codeBlock.body.isEmpty()) return;
        switch (codeBlock.where) {
            case AFTER_SUPER:
            case BEFORE_SUPER:
                if (!m.isSuper()) return;
                rb.append(codeBlock.body);
                break;
            case AFTER_A_CALL:
            case BEFORE_A_CALL:
            case AROUND_A_CALL:
                if (!m.getMethodName().equals(codeBlock.aroundMethodCall)) return;
                rb.append(codeBlock.body);
        }
        m.replace(rb.toString());
        super.edit(m);
    }

}
