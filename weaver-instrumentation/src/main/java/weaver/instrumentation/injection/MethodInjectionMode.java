package weaver.instrumentation.injection;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
enum MethodInjectionMode {
    AT_THE_BEGINNING,
    AT_THE_END,
    AFTER_SUPER,
    BEFORE_SUPER,
    AFTER_A_CALL,
    BEFORE_A_CALL
}
