package weaver.instrumentation.injection;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
interface ResourceBundle {
    CtClass getCtClass();

    ClassPool getPool();
}
