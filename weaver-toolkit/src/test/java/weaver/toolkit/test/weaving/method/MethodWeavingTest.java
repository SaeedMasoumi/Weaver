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
public class MethodWeavingTest extends WeavingSpec {

    @Test
    public void check() throws Exception {
        toolkit.startWeaving(ctClass)
                .inMethod("methodNotExistsWithoutParams")
                .createMethodIfNotExists()
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
                .inMethod("methodNotExistsWithParams", "int", "int")
                .createMethodIfNotExists()
                .modifiers(Modifier.PUBLIC)
                .returnType("int")
                .parametersName("a", "b")
                .body("{ " +
                        "int result = a*b;" +
                        "System.out.println(\"result = \"+result);" +
                        "return a*b; " +
                        "}")
                .done()
                .done();
        Class clazz = ctClass.toClass();
        Object object = clazz.newInstance();
        Method methodWithParams =
                clazz.getMethod("methodNotExistsWithParams", int.class, int.class);
        methodWithParams.setAccessible(true);
        Method methodWithoutParams =
                clazz.getMethod("methodNotExistsWithoutParams");
        methodWithoutParams.setAccessible(true);
        int result = (int) methodWithParams.invoke(object, 20, 2);
        Object condition = methodWithoutParams.invoke(object);

        assertThat(condition, instanceOf(Condition.class));
        assertEquals(result, 40);
    }

    @Override
    protected String getSampleClassName() {
        return Activity.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "MethodWeaving";
    }
}
