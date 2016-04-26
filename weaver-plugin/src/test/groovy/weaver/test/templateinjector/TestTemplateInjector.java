package weaver.test.templateinjector;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import weaver.plugin.internal.processor.injector.TemplateInjectorImp;
import weaver.processor.injector.TemplateInjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class TestTemplateInjector {

    private ClassPool pool;
    private CtClass ctSampleClass;
    private TemplateInjector templateInjector;

    @Before
    public void init() throws NotFoundException {
        pool = ClassPool.getDefault();
        ctSampleClass = pool.get(SampleClass.class.getCanonicalName());
        //to avoid exception while calling toClass()
        ctSampleClass.setName(SampleClass.class.getPackage().getName() + ".ConvertedSampleClass");
        templateInjector = new TemplateInjectorImp(ClassPool.getDefault());

    }

    @Test
    public void check_field_is_injected()
            throws IOException, CannotCompileException, NotFoundException, IllegalAccessException,
            InstantiationException, NoSuchFieldException {
        templateInjector.inject(SampleTemplate.class, ctSampleClass);
        Class sampleClass = ctSampleClass.toClass();
        Object instance = sampleClass.newInstance();
        assert sampleClass.getField("injectedField") != null;
    }
}
