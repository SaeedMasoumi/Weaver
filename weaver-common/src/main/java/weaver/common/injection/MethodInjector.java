package weaver.common.injection;

import java.lang.reflect.Type;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface MethodInjector<P extends ClassInjector> extends Injectable<P> {

    MethodInjectorExistsMode ifExists();

    MethodInjectorOverrideMode ifExistsButNotOverride();

    MethodInjectorNotExistsMode createIfNotExists();

    @Override
    P inject() throws Exception;

    interface MethodInjectorExistsMode<P extends MethodInjector> extends Injectable<P> {

        MethodInjectorExistsMode atTheBeginning(String statements);

        MethodInjectorExistsMode atTheEnd(String statements);

        MethodInjectorExistsMode afterSuper(String statements);

        MethodInjectorExistsMode beforeSuper(String statements);

        MethodInjectorExistsMode afterACallTo(String call, String statements);

        MethodInjectorExistsMode beforeACallTo(String call, String statements);

        @Override
        P inject() throws Exception;
    }

    interface MethodInjectorOverrideMode<P extends MethodInjector> extends Injectable<P> {

        MethodInjectorOverrideMode override(String fullMethodBody);

        @Override
        P inject() throws Exception;
    }

    interface MethodInjectorNotExistsMode<P extends MethodInjector> extends Injectable<P> {

        MethodInjectorNotExistsMode returns(Type clazz);

        MethodInjectorNotExistsMode returns(String fullQualifiedName);

        MethodInjectorNotExistsMode addModifiers(int... modifiers);

        MethodInjectorNotExistsMode setParametersName(String... parametersName);

        MethodInjectorNotExistsMode withBody(String body);

        @Override
        P inject() throws Exception;
    }

}
