package weaver.test.templateinjector

import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import org.junit.Before
import org.junit.Test
import weaver.plugin.internal.javassist.WeaverClassPool
import weaver.plugin.internal.processor.injector.JavassistTemplateInjector
import weaver.processor.TemplateInjector

import java.lang.reflect.Field

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TestJavassistTemplateInjector {
    private ClassPool pool
    private CtClass ctSampleClass
    private TemplateInjector templateInjector

    @Before
    void "init"() throws NotFoundException {
        pool = ClassPool.getDefault()
        ctSampleClass = pool.get(SampleClass.class.getCanonicalName())
        //to avoid exception while calling toClass()
        ctSampleClass.setName(SampleClass.class.getPackage().getName() + ".ConvertedSampleClass")
        WeaverClassPool pool = new WeaverClassPool(getClass().getClassLoader(), true)
        templateInjector = new JavassistTemplateInjector(pool)
    }

    @Test
    void "check fields are injected"()
            throws IOException, CannotCompileException, NotFoundException, IllegalAccessException,
                    InstantiationException, NoSuchFieldException {
        templateInjector.inject(SampleTemplate.class, ctSampleClass)
        Class sampleClass = ctSampleClass.toClass()
        //first constructor
        Object instance = sampleClass.newInstance()
        //second constructor
        Object instanceWithParams = sampleClass.newInstance(10)
        //clojure to get value form integer field
        def getInt = { String fieldName, Object ins ->
            Field field = sampleClass.getField(fieldName)
            field.setAccessible(true)
            field.getInt(ins)
        }
        assert getInt("field1", instance).equals(-1)
        assert getInt("field2", instance).equals(2)
        assert getInt("field1", instanceWithParams).equals(10)
        assert getInt("field2", instanceWithParams).equals(2)

    }
}
