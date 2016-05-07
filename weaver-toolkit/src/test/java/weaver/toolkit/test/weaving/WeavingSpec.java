package weaver.toolkit.test.weaving;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import weaver.toolkit.WeaverToolkit;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class WeavingSpec {

    protected WeaverToolkit toolkit;
    protected CtClass ctClass;

    protected <T> T invokeMethod(Class<T> type, String methodName,
                                 Object instance) {
        try {
            Method method = instance.getClass().getMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(instance);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Before
    public void initClassPool() throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        toolkit = new WeaverToolkit(pool);
        ctClass = pool.get(getSampleClassName());
        ctClass.setName(ctClass.getName() + getTransformedName());
    }

    protected abstract String getSampleClassName();

    protected abstract String getTransformedName();

    @After
    public void storeCtClass() throws CannotCompileException, IOException {
        ctClass.writeFile(getClass().getResource("/").getPath());
    }
}
