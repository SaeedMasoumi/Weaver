package weaver.toolkit.test.weaving.interfaces;

import org.junit.Test;

import weaver.toolkit.test.weaving.WeavingSpec;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class InterfaceWeavingTest extends WeavingSpec {

    @Test
    public void make_sure_interfaces_are_injected_into_class()
            throws Exception {
        toolkit.startWeaving(ctClass)
                .insertInterface()
                .name(SampleInterface.class.getCanonicalName())
                .name(SampleInterface2.class.getCanonicalName())
                .done();
        Object clazz = ctClass.toClass().newInstance();
        assertThat(clazz, instanceOf(SampleInterface.class));
        assertThat(clazz, instanceOf(SampleInterface2.class));
        assertThat(clazz, instanceOf(SampleParentInterface.class));
        assertThat(clazz, instanceOf(SampleExistingInterface.class));
    }


    @Override
    protected String getSampleClassName() {
        return SampleClass.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "InterfaceWeaving";
    }
}
