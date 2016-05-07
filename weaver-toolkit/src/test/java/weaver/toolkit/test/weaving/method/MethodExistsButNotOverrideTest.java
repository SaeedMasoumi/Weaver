package weaver.toolkit.test.weaving.method;

import org.junit.Test;

import javassist.CtMethod;
import javassist.Modifier;
import weaver.toolkit.internal.JavassistUtils;
import weaver.toolkit.test.weaving.WeavingSpec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class MethodExistsButNotOverrideTest extends WeavingSpec {

    @Test
    public void check_methods_are_override() throws Exception {
        toolkit.startWeaving(ctClass)
                //insert Interface
                .insertInterface()
                .name(DelegateInterface.class.getCanonicalName())
                .done()
                //implement its delegate method
                .insertMethod("delegate")
                .ifExistsButNotOverride()
                .override("{" +
                        "System.out.println(\"delegate\");" +
                        "}")
                .done()
                .done()
                //insert a private field
                .insertField("delegate")
                .instantiateIt()
                .type(Delegate_MyClass.class.getCanonicalName())
                .modifiers(Modifier.PRIVATE)
                .done()
                //override onResume
                .insertMethod("onResume", Bundle.class.getCanonicalName())
                .ifExistsButNotOverride()
                .override("{" +
                        "System.out.println(\"before super\");" +
                        "super.onResume($$);" +
                        "System.out.println(\"after super \"+delegate);\n" +
                        "}")
                .done()
                .done()
                //override on pause
                .insertMethod("onPause")
                .ifExistsButNotOverride()
                .override("{" +
                        "return 10;" +
                        "}")
                .done()
                .done()
                //override on stop
                .insertMethod("onStop")
                .ifExistsButNotOverride()
                .override("{" +
                        "return super.onStop();" +
                        "}")
                .done()
                .done()
        ;

        CtMethod onResumeMethod = JavassistUtils.findMethod("onResume",
                new String[] {Bundle.class.getCanonicalName()},
                ctClass.getDeclaredMethods());
        CtMethod delegateMethod =
                JavassistUtils.findMethod("delegate", new String[0], ctClass.getDeclaredMethods());
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
