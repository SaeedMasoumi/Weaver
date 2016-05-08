package weaver.common.injection;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface InterfaceInjector<P extends ClassInjector> extends Injectable<P> {

    InterfaceInjector implement(String fullQualifiedName);

    InterfaceInjector implement(Class<?> interfaceClass);

    @Override
    P inject() throws Exception;
}
