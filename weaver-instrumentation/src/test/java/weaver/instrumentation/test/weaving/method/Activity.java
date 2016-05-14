package weaver.instrumentation.test.weaving.method;

import java.util.ArrayList;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Activity extends ParentActivity {

    public ArrayList<Integer> array = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        foo();
        bar();
    }

    private void foo() {

    }

    private void bar() {

    }
}
