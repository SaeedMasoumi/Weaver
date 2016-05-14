package weaver.instrumentation.test.weaving.method;

import java.util.ArrayList;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Activity extends ParentActivity {

    public ArrayList<Integer> array = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        bar();
        super.onCreate(bundle);
        foo();
        finish();
    }

    private void finish() {
        array = null;
    }

    private void bar() {

    }

    private void foo() {

    }


}
