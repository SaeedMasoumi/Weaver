package weaver.instrumentation.test.weaving.method;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import weaver.instrumentation.test.weaving.WeavingSpec;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class MethodExistsTest extends WeavingSpec {

    @Test
    public void test_all_aspects_of_method_statement() throws Exception {
        instrumentation.startWeaving(ctClass)
                .insertMethod("onCreate", Bundle.class.getCanonicalName())
                .ifExists()
                .atTheEnd("array.add(new Integer(4));")
                .afterSuper("array.add(new Integer(3));")
                .atTheBeginning("array.add(new Integer(1));")
                .beforeSuper("array.add(new Integer(2));")
                .atTheBeginning("System.out.println(\"start\");")
                .inject()
                .inject();
        Object instance = ctClass.toClass().newInstance();
        Field field = instance.getClass().getField("array");
        ArrayList<Integer> arrays = (ArrayList<Integer>) field.get(instance);
        //assert orders by calling
        invokeMethod(void.class, "onCreate", instance, new Class[] {Bundle.class}, new Object[]{new Bundle()});
        for (int i = 1; i <= arrays.size(); i++) {
            assertEquals(new Integer(i), arrays.get(i - 1));
        }
    }

    @Override
    protected String getSampleClassName() {
        return Activity.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "MethodExisting";
    }
}
