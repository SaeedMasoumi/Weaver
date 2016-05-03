package weaver.test.templateinjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class SampleClass extends SampleAncestor {

    public SampleClass() {

    }

    public SampleClass(int foo) {

    }

    public void methodForInjection() {

    }

    @Override
    public void methodForInjectionWithSuper() {
        System.out.println("method called before super");
        super.methodForInjectionWithSuper();
        System.out.println("method called after super");
    }

    @Override
    public void methodForInjectionWithSuper2() {
    }
}
