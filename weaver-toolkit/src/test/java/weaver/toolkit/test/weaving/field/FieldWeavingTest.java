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
            throws Exception{
        toolkit.startWeaving(ctClass)
                .addField()
                .modifier(Modifier.PUBLIC)
                .type(Point.class.getCanonicalName())
                .name("point")
                .newInstance()
                .done();
        Field field = ctClass.toClass().getField("point");
        assertNotNull(field);
        assertEquals(field.getName(), "point");

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
