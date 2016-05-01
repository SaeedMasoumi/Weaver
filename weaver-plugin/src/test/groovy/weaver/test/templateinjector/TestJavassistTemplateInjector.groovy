package weaver.test.templateinjector

import javassist.CannotCompileException
import javassist.CtClass
import javassist.NotFoundException
import org.junit.Before
import org.junit.Test
import weaver.plugin.internal.javassist.WeaverClassPool
import weaver.plugin.internal.processor.injector.JavassistTemplateInjector
import weaver.processor.TemplateInjector
import weaver.test.Directories

import java.lang.reflect.Field

import static junit.framework.Assert.assertEquals

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TestJavassistTemplateInjector {
    private WeaverClassPool pool
    private CtClass ctSampleClass
    private TemplateInjector templateInjector

    @Before
    void "init"() throws NotFoundException {
        pool = new WeaverClassPool(getClass().getClassLoader(), true)
        pool.setCachedCompiler(Directories.OUTPUT_DIR)
        ctSampleClass = pool.get(SampleClass.class.getCanonicalName())
        //to avoid exception while calling toClass()
        ctSampleClass.setName(SampleClass.class.getPackage().getName() + ".ConvertedSampleClass")
        templateInjector = new JavassistTemplateInjector(pool)
    }

    @Test
    void "check fields are injected"()
            throws IOException, CannotCompileException, NotFoundException, IllegalAccessException,
                    InstantiationException, NoSuchFieldException {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class TemplateClass {\n" +
                " public int field1;\n" +
                " public int field2 = 2;\n" +
                " public TemplateClass() {\n" +
                "    field1 = -1;\n" +
                "}\n" +
                "public TemplateClass(int foo) {\n" +
                "   field1 = foo;\n" +
                "}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.TemplateClass",
                templateClass, ctSampleClass)
        Class sampleClass = ctSampleClass.toClass()
        //first constructor
        Object instance = sampleClass.newInstance()
        //second constructor
        Object instanceWithParams = sampleClass.newInstance(10)
        //clojure to get integer value from field
        def getInt = { String fieldName, Object ins ->
            Field field = sampleClass.getField(fieldName)
            field.setAccessible(true)
            field.getInt(ins)
        }
        assertEquals(getInt("field1", instance), -1)
        assertEquals(getInt("field1", instanceWithParams), 10)
        assertEquals(getInt("field2", instance), 2)
        assertEquals(getInt("field2", instanceWithParams), 2)
        ctSampleClass.writeFile(Directories.OUTPUT_DIR.absolutePath)
    }

}
