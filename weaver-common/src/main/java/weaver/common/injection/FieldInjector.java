package weaver.common.injection;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface FieldInjector<P extends ClassInjector> extends Injectable<P> {

    FieldInjector addModifiers(int... modifiers);

    FieldInjector addSetter();

    FieldInjector addGetter();

    FieldInjector initializeIt();

    FieldInjector withInitializer(String value);

    @Override
    P inject() throws Exception;
}
