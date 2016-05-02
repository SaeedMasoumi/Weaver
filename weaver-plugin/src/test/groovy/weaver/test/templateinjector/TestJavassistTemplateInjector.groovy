package weaver.test.templateinjector

import javassist.CannotCompileException
import javassist.CtClass
import javassist.NotFoundException
import org.junit.After
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

    public TestJavassistTemplateInjector() {
        pool = new WeaverClassPool(getClass().getClassLoader(), true)
        pool.setCachedCompiler(Directories.OUTPUT_DIR)
        ctSampleClass = pool.get(SampleClass.class.getCanonicalName())
        //to avoid exception while calling toClass()
        ctSampleClass.setName(SampleClass.class.getPackage().getName() + ".ConvertedSampleClass")
        templateInjector = new JavassistTemplateInjector(pool)
    }

    @Test
    void "validate"() {
        //TODO junit sucks, switch to junit5 for @BeforeAll
        "check interfaces are injected"()
        "check fields are injected"()
        "check existing constructor injected"()
        "check non-existing constructor injected"()
        //asserting
        Class sampleClass = ctSampleClass.toClass()
        //first constructor
        Object instance = sampleClass.newInstance()
        //second constructor
        Object instanceWithParams = sampleClass.newInstance(10)
        Object instanceWithBoolParams = sampleClass.newInstance(true)
        //clojure to get integer value from field
        def getInt = { String fieldName, Object ins ->
            Field field = sampleClass.getField(fieldName)
            field.setAccessible(true)
            field.getInt(ins)
        }
        //clojure to get boolean value from field
        def getBool = { String fieldName, Object ins ->
            Field field = sampleClass.getField(fieldName)
            field.setAccessible(true)
            field.getBoolean(ins)
        }
        assertEquals(getInt("field1", instance), -1)
        assertEquals(getInt("field1", instanceWithParams), 10)
        assertEquals(getInt("field2", instance), 2)
        assertEquals(getInt("field2", instanceWithParams), 2)
        assertEquals(getBool("field3", instanceWithBoolParams), true)
        assert instance as Runnable
        assert instanceWithParams as Runnable
    }

    void "check interfaces are injected"() {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class InterfaceTemplate implements Runnable{\n" +
                "@Override" +
                " public void run(){}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.InterfaceTemplate",
                templateClass, ctSampleClass)
    }


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
    }

    void "check existing constructor injected"() {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class ExistingConstructorTemplate {\n" +
                " int i = 1;" +
                " public ExistingConstructorTemplate() {\n" +
                "    System.out.println(\"Empty constructor called \"+i);\n" +
                "}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.ExistingConstructorTemplate",
                templateClass, ctSampleClass)
    }

    void "check non-existing constructor injected"() {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class ConstructorTemplate {\n" +
                " int i = 0;" +
                " public boolean field3 = false;\n" +
                " public ConstructorTemplate(boolean aBoolean) {\n" +
                " field3 = aBoolean;\n" +
                "}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.ConstructorTemplate",
                templateClass, ctSampleClass)
    }

    @After
    void "copy CtClass"() {
        ctSampleClass.writeFile(Directories.OUTPUT_DIR.absolutePath)
    }
}
