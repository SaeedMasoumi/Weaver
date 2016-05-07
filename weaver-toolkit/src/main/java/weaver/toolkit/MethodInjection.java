package weaver.toolkit;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
enum MethodInjection {
    AT_THE_BEGINNING,
    AT_THE_END,
    AFTER_SUPER,
    BEFORE_SUPER,
    AFTER_A_CALL,
    BEFORE_A_CALL;

    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
