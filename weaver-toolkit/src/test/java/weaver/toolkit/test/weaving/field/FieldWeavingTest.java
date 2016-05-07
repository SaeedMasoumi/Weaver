package weaver.toolkit.test.weaving.field;

import org.junit.Test;

import java.lang.reflect.Field;

import javassist.Modifier;
import weaver.toolkit.test.weaving.WeavingSpec;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class FieldWeavingTest extends WeavingSpec {

    @Test
    public void test_filed_injection()
            throws Exception {
        toolkit.startWeaving(ctClass)
                .insertField("point")
                .modifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .type(Point.class.getCanonicalName())
                .instantiateIt()
                .done()
                .insertField("integer")
                .modifiers(Modifier.PUBLIC)
                .type(int.class.getCanonicalName())
                .instantiate("2")
                .addGetter()
                .addSetter()
                .done()
                .insertField("nullPoint")
                .modifiers(Modifier.PUBLIC)
                .type(Point.class.getCanonicalName())
                .done();


        Object instance = ctClass.toClass().newInstance();
        Field point = instance.getClass().getField("point");
        Field integer = instance.getClass().getField("integer");
        Field nullPoint = instance.getClass().getField("nullPoint");

        assertNotNull(point);
        assertNotNull(integer);
        assertNotNull(nullPoint);

        assertEquals(integer.get(instance), 2);
        assertNull(nullPoint.get(instance));
        assertNotNull(point.get(instance));

        invokeMethod(void.class, "setInteger", instance, new Class[] {int.class}, new Object[] {3});
        int result = invokeMethod(int.class, "getInteger", instance);
        assertEquals(result, 3);
    }

    @Override
    protected String getSampleClassName() {
        return SampleFieldClass.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "FieldTransformed";
    }
}
