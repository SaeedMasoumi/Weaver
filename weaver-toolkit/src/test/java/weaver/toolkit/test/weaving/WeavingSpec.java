package weaver.toolkit.test.weaving;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

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
