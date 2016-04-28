package weaver.test.templateinjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class SampleTemplate {

    public int field1;
    public int field2 = 2;

    public SampleTemplate() {
        field1 = -1;
    }

    public SampleTemplate(int foo) {
        field1 = foo;
    }
}
