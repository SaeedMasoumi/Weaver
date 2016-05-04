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
import java.lang.reflect.Method

import static junit.framework.Assert.*

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
        "inject interfaces"()
        "inject fields"()
        "inject existing constructors"()
        "inject non-existing constructors"()
        "inject method at beginning"()
        "inject method before return"()
        "inject method after/before super"()
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
        def invokeMethod = {
            String methodName, Object ins, Class<?>... paramsClass ->
                Method method = sampleClass.getMethod(methodName, paramsClass)
                method.setAccessible(true)
                method.invoke(ins)
        }
        assertEquals(getInt("field1", instance), -1)
        assertEquals(getInt("field1", instanceWithParams), 10)
        assertEquals(getInt("field2", instance), 2)
        assertEquals(getInt("field2", instanceWithParams), 2)
        assertTrue(getBool("field3", instanceWithBoolParams))
        //check method injection
        invokeMethod("methodForInjection", instance)
        invokeMethod("methodForInjectionWithSuper", instance)
        invokeMethod("methodForInjectionWithSuper2", instance)

        assertTrue(getBool("atBeginning", instance))
        assertTrue(getBool("beforeReturn", instance))
        assertTrue(getBool("afterSuper", instance))
        assertFalse(getBool("afterSuper2", instance))
        assertTrue(getBool("beforeSuper", instance))
        assertFalse(getBool("beforeSuper2", instance))
        assert instance as Runnable
        assert instanceWithParams as Runnable
    }

    void "inject interfaces"() {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class InterfaceTemplate implements Runnable{\n" +
                "@Override" +
                " public void run(){}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.InterfaceTemplate",
                templateClass, ctSampleClass)
    }


    void "inject fields"()
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

    void "inject existing constructors"() {
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

    void "inject non-existing constructors"() {
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

    void "inject method at beginning"() {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class MethodAtBeginningTemplate {\n" +
                " public boolean atBeginning = false;\n" +
                " public void methodForInjection\$\$AtBeginning() {\n" +
                " atBeginning = true;\n" +
                "}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.MethodAtBeginningTemplate",
                templateClass, ctSampleClass)
    }

    void "inject method before return"() {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class MethodBeforeReturnTemplate {\n" +
                " public boolean beforeReturn = false;\n" +
                " public void methodForInjection\$\$BeforeReturn() {\n" +
                " beforeReturn = true;\n" +
                "}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.MethodBeforeReturnTemplate",
                templateClass, ctSampleClass)
    }

    void "inject method after/before super"() {
        String templateClass = "\n" +
                "package io.saeid.weaver.test.templateinject;" +
                "class MethodAfterSuperTemplate {\n" +
                " public boolean afterSuper = false;\n" +
                " public boolean afterSuper2 = false;\n" +
                " public boolean beforeSuper = false;\n" +
                " public boolean beforeSuper2 = false;\n" +
                " public void methodForInjectionWithSuper\$\$AfterSuper() {\n" +
                " afterSuper = true;\n" +
                " System.out.println(this+\" weaving method called in child after super\");\n" +
                "}\n" +
                " public void methodForInjectionWithSuper2\$\$AfterSuper() {\n" +
                " afterSuper2 = true;\n" +
                "}\n" +
                " public void methodForInjectionWithSuper\$\$BeforeSuper() {\n" +
                " beforeSuper = true;\n" +
                " System.out.println(this+\" weaving method called in child before super\");\n" +
                "}\n" +
                " public void methodForInjectionWithSuper2\$\$BeforeSuper() {\n" +
                " beforeSuper2 = true;\n" +
                "}\n" +
                "}";
        templateInjector.inject("io.saeid.weaver.test.templateinject.MethodAfterSuperTemplate",
                templateClass, ctSampleClass)
    }

    @After
    void "copy CtClass"() {
        ctSampleClass.writeFile(Directories.OUTPUT_DIR.absolutePath)
    }
}
