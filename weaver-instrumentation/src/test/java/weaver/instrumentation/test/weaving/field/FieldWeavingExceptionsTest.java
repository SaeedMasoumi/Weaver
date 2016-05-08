package weaver.instrumentation.test.weaving.field;

import org.junit.Test;

import javassist.Modifier;
import javassist.bytecode.DuplicateMemberException;
import weaver.instrumentation.test.weaving.WeavingSpec;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class FieldWeavingExceptionsTest extends WeavingSpec {

    @Test(expected = DuplicateMemberException.class)
    public void duplicate_field_exception() throws Exception {
        //inserting on an existing field
        instrumentation.startWeaving(ctClass)
                .insertField(int.class, "foo")
                .addModifiers(Modifier.PUBLIC)
                .withInitializer("1")
                .inject();
    }

    @Override
    protected String getSampleClassName() {
        return SampleFieldClass.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "FieldTestTransformed";
    }
}
