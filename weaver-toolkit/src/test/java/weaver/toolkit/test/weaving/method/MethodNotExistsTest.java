package weaver.toolkit.test.weaving.method;

import org.junit.Test;

import java.lang.reflect.Method;

import javassist.Modifier;
import weaver.toolkit.test.weaving.WeavingSpec;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class MethodNotExistsTest extends WeavingSpec {

    @Test
    public void make_sure_all_different_behaviours_are_injected() throws Exception {
        toolkit.startWeaving(ctClass)
                .insertField()
                .modifiers(Modifier.PRIVATE)
                .type(Condition.class.getCanonicalName())
                .name("conditionField")
                .instantiateIt()
                .done()
                .insertMethod("methodNotExistsWithoutParams")
                .createIfNotExists()
                .modifiers(Modifier.PUBLIC)
                .returnType(Condition.class.getCanonicalName())
                .body("{" +
                        "System.out.println(\"Method without params called\");" +
                        "return new " +
                        Condition.class.getCanonicalName() +
                        "();" +
                        "}")
                .done()
                .done()
                .insertMethod("getConditionFromField")
                .createIfNotExists()
                .modifiers(Modifier.PUBLIC)
                .returnType(Condition.class.getCanonicalName())
                .body("{" +
                        "System.out.println(\"Condition in field\"+conditionField);" +
                        "return conditionField;" +
                        "}")
                .done()
                .done()
                .insertMethod("methodNotExistsWithParams", "int", "int")
                .createIfNotExists()
                .modifiers(Modifier.PUBLIC)
                .returnType("int")
                .parametersName("a", "b")
                .body("{ " +
                        "int result = a*b;" +
                        "System.out.println(\"result = \"+result);" +
                        "return a*b; " +
                        "}")
                .done()
                .done()
                .insertMethod("methodNotExistsWithParamsWithoutParamNames", "boolean")
                .createIfNotExists()
                .modifiers(Modifier.PUBLIC)
                .returnType("boolean")
                .body("{ " +
                        "return p1; " +
                        "}")
                .done()
                .done();
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
