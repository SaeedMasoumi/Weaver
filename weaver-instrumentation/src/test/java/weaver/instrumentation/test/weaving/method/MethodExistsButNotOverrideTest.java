package weaver.instrumentation.test.weaving.method;

import org.junit.Test;

import javassist.CtMethod;
import javassist.Modifier;
import weaver.instrumentation.injection.InternalUtils;
import weaver.instrumentation.test.weaving.WeavingSpec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class MethodExistsButNotOverrideTest extends WeavingSpec {

    @Test
    public void check_methods_are_override() throws Exception {
        instrumentation.startWeaving(ctClass)
                //insert Interface
                .insertInterface()
                .implement(DelegateInterface.class.getCanonicalName())
                .inject()
                //implement its delegate method
                .insertMethod("delegate")
                .ifExistsButNotOverride()
                .override("{" +
                        "System.out.println(\"delegate\");" +
                        "}")
                .inject()
                .inject()
                //insert a private field
                .insertField(Delegate_MyClass.class, "delegate")
                .initializeIt()
                .addModifiers(Modifier.PRIVATE)
                .inject()
                //override onResume
                .insertMethod("onResume", Bundle.class)
                .ifExistsButNotOverride()
                .override("{" +
                        "System.out.println(\"before super\");" +
                        "super.onResume($$);" +
                        "System.out.println(\"after super \"+delegate);\n" +
                        "}")
                .inject()
                .inject()
                //override on pause
                .insertMethod("onPause")
                .ifExistsButNotOverride()
                .override("{" +
                        "return 10;" +
                        "}")
                .inject()
                .inject()
                //override on stop
                .insertMethod("onStop")
                .ifExistsButNotOverride()
                .override("{" +
                        "return super.onStop();" +
                        "}")
                .inject()
                .inject()
        ;

        CtMethod onResumeMethod = InternalUtils.findMethod("onResume",
                new String[] {Bundle.class.getCanonicalName()},
                ctClass.getDeclaredMethods());
        CtMethod delegateMethod =
                InternalUtils.findMethod("delegate", new String[0], ctClass.getDeclaredMethods());
        assertNotNull(onResumeMethod);
        assertNotNull(delegateMethod);

        Object obj = ctClass.toClass().newInstance();
        int result = invokeMethod(int.class, "onPause", obj);
        boolean stop = invokeMethod(boolean.class, "onStop", obj);
        assertEquals(result, 10);
        assertEquals(stop, true);
    }

    @Override
    protected String getSampleClassName() {
        return Activity.class.getCanonicalName();
    }

    @Override
    protected String getTransformedName() {
        return "Override";
    }
}
