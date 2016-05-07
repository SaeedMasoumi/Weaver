package weaver.toolkit.test.weaving.field;

import org.junit.Test;

import java.lang.reflect.Field;

import javassist.Modifier;
import weaver.toolkit.test.weaving.WeavingSpec;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class FieldWeavingTest extends WeavingSpec {

    @Test
    public void test_filed_injection()
            throws Exception {
        toolkit.startWeaving(ctClass)
                .insertField()
                    .modifiers(Modifier.PUBLIC)
                    .type(Point.class.getCanonicalName())
                    .name("point")
                    .done()
                .insertField()
                    .modifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .type(Point.class.getCanonicalName())
                    .name("pointFinal")
                    .instantiateIt()
                    .done();
        Class clazz = ctClass.toClass();
        Field point = clazz.getField("point");
        Field pointFinal = clazz.getField("pointFinal");
        assertNotNull(point);
        assertEquals(point.getName(), "point");
        assertNotNull(pointFinal);
        assertEquals(pointFinal.getName(), "pointFinal");

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
