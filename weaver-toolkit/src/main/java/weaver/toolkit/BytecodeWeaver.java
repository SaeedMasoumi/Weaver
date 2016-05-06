package weaver.toolkit;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
abstract class BytecodeWeaver<Parent extends ResourceBundle> implements ResourceBundle {

    private Parent parent;

    BytecodeWeaver(Parent parent) {
        this.parent = parent;
    }

    @Override
    public CtClass getCtClass() {
        return parent.getCtClass();
    }

    @Override
    public ClassPool getPool() {
        return parent.getPool();
    }

    protected abstract void weaving() throws Exception;

    public Parent done() throws Exception {
        weaving();
        return parent;
    }


}
