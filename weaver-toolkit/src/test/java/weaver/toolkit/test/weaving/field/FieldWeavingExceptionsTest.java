package weaver.toolkit.test.weaving.field;

import org.junit.Test;

import javassist.Modifier;
import javassist.bytecode.DuplicateMemberException;
import weaver.toolkit.test.weaving.WeavingSpec;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class FieldWeavingExceptionsTest extends WeavingSpec {

    @Test(expected = DuplicateMemberException.class)
    public void duplicate_field_exception() throws Exception {
        //inserting on an existing field
            toolkit.startWeaving(ctClass)
                    .insertField("foo")
                    .modifiers(Modifier.PUBLIC)
                    .type(int.class.getCanonicalName())
                    .instantiate("1")
                    .done();
    }

    @Override
    protected String getSampleClassName() {
        return SampleFieldClass.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "fieldWeavingException";
    }
}
