package weaver.instrumentation.test.weaving.interfaces;

import org.junit.Test;

import weaver.instrumentation.test.weaving.WeavingSpec;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class InterfaceWeavingTest extends WeavingSpec {

    @Test
    public void make_sure_interfaces_are_injected_into_class()
            throws Exception {
        instrumentation.startWeaving(ctClass)
                .insertInterface()
                .implement(SampleInterface.class.getCanonicalName())
                .implement(SampleInterface2.class)
                .inject();
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
