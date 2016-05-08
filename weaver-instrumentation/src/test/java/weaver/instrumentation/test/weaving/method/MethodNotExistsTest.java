package weaver.instrumentation.test.weaving.method;

import org.junit.Test;

import java.lang.reflect.Method;

import javassist.Modifier;
import weaver.instrumentation.test.weaving.WeavingSpec;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class MethodNotExistsTest extends WeavingSpec {

    @Test
    public void make_sure_all_different_behaviours_are_injected() throws Exception {
        instrumentation.startWeaving(ctClass)
                .insertField(Condition.class, "conditionField")
                .addModifiers(Modifier.PRIVATE)
                .initializeIt()
                .inject()
                .insertMethod("methodNotExistsWithoutParams")
                .createIfNotExists()
                .addModifiers(Modifier.PUBLIC)
                .returns(Condition.class.getCanonicalName())
                .withBody("{" +
                        "System.out.println(\"Method without params called\");" +
                        "return new " +
                        Condition.class.getCanonicalName() +
                        "();" +
                        "}")
                .inject()
                .inject()
                .insertMethod("getConditionFromField")
                .createIfNotExists()
                .addModifiers(Modifier.PUBLIC)
                .returns(Condition.class.getCanonicalName())
                .withBody("{" +
                        "System.out.println(\"Condition in field\"+conditionField);" +
                        "return conditionField;" +
                        "}")
                .inject()
                .inject()
                .insertMethod("methodNotExistsWithParams", "int", "int")
                .createIfNotExists()
                .addModifiers(Modifier.PUBLIC)
                .returns("int")
                .setParametersName("a", "b")
                .withBody("{ " +
                        "int result = a*b;" +
                        "System.out.println(\"result = \"+result);" +
                        "return a*b; " +
                        "}")
                .inject()
                .inject()
                .insertMethod("methodNotExistsWithParamsWithoutParamNames", "boolean")
                .createIfNotExists()
                .addModifiers(Modifier.PUBLIC)
                .returns("boolean")
                .withBody("{ " +
                        "return p1; " +
                        "}")
                .inject()
                .inject();
        Class clazz = ctClass.toClass();
        Object object = clazz.newInstance();
        Method methodWithParams =
                clazz.getMethod("methodNotExistsWithParams", int.class, int.class);
        Method methodWithoutParams =
                clazz.getMethod("methodNotExistsWithoutParams");
        Method methodWithoutParamsWithoutParamNames =
                clazz.getMethod("methodNotExistsWithParamsWithoutParamNames", boolean.class);
        Method getConditionFromField = clazz.getMethod("getConditionFromField");

        methodWithParams.setAccessible(true);
        methodWithoutParams.setAccessible(true);
        methodWithoutParamsWithoutParamNames.setAccessible(true);
        getConditionFromField.setAccessible(true);

        int result = (int) methodWithParams.invoke(object, 20, 2);
        Object condition = methodWithoutParams.invoke(object);
        boolean trueCondition = (boolean) methodWithoutParamsWithoutParamNames.invoke(object, true);
        boolean falseCondition =
                (boolean) methodWithoutParamsWithoutParamNames.invoke(object, false);
        Object conditionFromField = getConditionFromField.invoke(object);

        assertThat(condition, instanceOf(Condition.class));
        assertThat(conditionFromField, instanceOf(Condition.class));
        assertEquals(result, 40);
        assertEquals(trueCondition, true);
        assertEquals(falseCondition, false);
    }

    @Override
    protected String getSampleClassName() {
        return Activity.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "NotExists";
    }
}
