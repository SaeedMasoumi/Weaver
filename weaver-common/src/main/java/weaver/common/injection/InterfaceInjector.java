package weaver.common.injection;

import java.lang.reflect.Type;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface InterfaceInjector<P extends ClassInjector> extends Injectable<P> {

    InterfaceInjector implement(String fullQualifiedName);

    InterfaceInjector implement(Class clazz);

    @Override
    P inject() throws Exception;
}
