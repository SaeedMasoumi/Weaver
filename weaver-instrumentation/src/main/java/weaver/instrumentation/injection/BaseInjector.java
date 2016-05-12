package weaver.instrumentation.injection;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
abstract class BaseInjector<Parent extends ResourceBundle>
        implements ResourceBundle {
    protected Parent parent;

    BaseInjector(Parent parent) {
        this.parent = parent;
    }

    public abstract Parent inject() throws Exception;

    @Override
    public CtClass getCtClass() {
        return parent.getCtClass();
    }

    @Override
    public ClassPool getPool() {
        return parent.getPool();
    }

}
