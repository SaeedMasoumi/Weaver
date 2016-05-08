package weaver.common.injection;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface ClassInjector {

    InterfaceInjector insertInterface();

    FieldInjector insertField(Class<?> type, String name);

    FieldInjector insertField(String type, String name);

    MethodInjector insertMethod(String methodName);

    MethodInjector insertMethod(String methodName, Class<?>... parameters);

    MethodInjector insertMethod(String methodName, String... parameters);
}
