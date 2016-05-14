package weaver.instrumentation.test.weaving.method;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class ParentActivity extends AncestorActivity {


    public void onCreate(Bundle bundle){

    }
    protected void onResume(Bundle bundle) {
        System.out.println("onResume in parentActivity");
    }

    public void onDestroy(){

    }
}
