package weaver.plugin.internal.processor.injector;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import weaver.plugin.internal.exception.FieldAlreadyExistsException;
import weaver.plugin.internal.javassist.WeaverClassPool;
import weaver.processor.TemplateInjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class JavassistTemplateInjector implements TemplateInjector {
    private WeaverClassPool pool;

    public JavassistTemplateInjector(WeaverClassPool pool) {
        this.pool = pool;
    }


    @Override
    public void inject(String templateClassName, String templateJavaCode, CtClass sourceClass) {
        try {
            CtClass templateCtClass = pool.getFromJavaCode(templateClassName, templateJavaCode);
            inject(templateCtClass, sourceClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inject(Class templateClass, CtClass sourceCtClass) {
        try {
            CtClass templateCtClass = pool.get(templateClass.getCanonicalName());
            inject(templateCtClass, sourceCtClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inject(CtClass template, CtClass source)
            throws Exception {
        source.defrost();
        injectInterfaces(template, source);
        injectFields(template, source);
        injectConstructors(template, source);
        injectMethods(template, source);
    }

    /**
     * Copies all implemented interfaces from template class intro source class. It will not inject
     * same implemented interfaces.
     *
     * @param template Given template CtClass.
     * @param source   Given source CtClass.
     * @throws NotFoundException thrown by javassist.
     */
    private void injectInterfaces(CtClass template, CtClass source) throws NotFoundException {
        for (CtClass interfaceClass : template.getInterfaces()) {
            if (!hasInterface(source, interfaceClass)) {
                source.addInterface(interfaceClass);
            }
        }
    }

    /**
     * Copies all constructors from template class into source class. If same constructor signature
     * exists in both sides, then it will create a <code>weaver__injected__constructor($)</code>
     * method in source class and call it at the end of constructor body.
     * <p>
     * Also initialization statements such as:
     * <p>
     * <code> boolean someValue = true; </code>
     * <p>
     * are moved from template constructor into injected constructor as well.
     *
     * @param template Given template CtClass.
     * @param source   Given source CtClass.
     * @throws CannotCompileException thrown by javassist.
     * @throws NotFoundException      thrown by javassist.
     */
    private void injectConstructors(CtClass template, CtClass source)
            throws CannotCompileException, NotFoundException {
        for (CtConstructor constructorInTemplate : template.getDeclaredConstructors()) {
            boolean constructorAdded = false;
            String methodName = "constructorWeaving$$" + constructorInTemplate.getName();
            for (CtConstructor constructorInSource : source.getDeclaredConstructors()) {

                if (hasSameConstructor(constructorInSource, constructorInTemplate)) {
                    source.addMethod(
                            constructorInTemplate.toMethod(methodName, source));
                    String string =
                            getNormalizedParameters(methodName, constructorInTemplate);
                    constructorInSource.insertAfter(string);
                    constructorAdded = true;
                }
            }
            if (!constructorAdded) {
                source.addConstructor(
                        CtNewConstructor.copy(constructorInTemplate, source, null));
            }
        }
    }

    /**
     * Copies all fields from template class into source class.
     *
     * @param template Given template CtClass.
     * @param source   Given source CtClass.
     * @throws FieldAlreadyExistsException If a field with same name already exists in source
     *                                     class.
     */
    private void injectFields(CtClass template, CtClass source) throws Exception {
        for (CtField field : template.getDeclaredFields()) {
            if (!hasField(field, source)) {
                source.addField(new CtField(field, source));
            }
        }
    }

    private void injectMethods(CtClass template, CtClass source)
            throws CannotCompileException, NotFoundException {
        for (CtMethod methodInTemplate : template.getDeclaredMethods()) {
            boolean IsMethodInserted = false;
            MethodInjectionMode injectionMode = MethodInjectionMode.getType(methodInTemplate);
            for (CtMethod methodInSource : source.getDeclaredMethods()) {
                if (hasSameMethod(methodInSource, methodInTemplate, injectionMode)) {
                    String methodName = "methodWeaving$$" + methodInTemplate.getName();
                    source.addMethod(CtNewMethod.copy(methodInTemplate, methodName, source, null));
                    String methodCall = getNormalizedParameters(methodName, methodInTemplate);
                    switch (injectionMode) {
                        case AT_BEGINNING:
                            methodInSource.insertBefore(methodCall);
                            break;
                        case BEFORE_RETURN:
                            methodInSource.insertAfter(methodCall);
                            break;
                        default:
                            MethodExprEditor editor = new MethodExprEditor(injectionMode,methodCall);
                            methodInSource.instrument(editor);
                    }
                    IsMethodInserted = true;
                }
            }
            if (!IsMethodInserted) {
                source.addMethod(CtNewMethod.copy(methodInTemplate, source, null));
            }
        }
    }

    private boolean hasInterface(CtClass clazz, CtClass interfaze) throws NotFoundException {
        for (CtClass interfaceClass : clazz.getInterfaces()) {
            if (interfaze.getName().equals(interfaceClass.getName())) return true;
        }
        return false;
    }

    private boolean hasField(CtField field, CtClass clazz) {
        try {
            clazz.getField(field.getName());
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private boolean hasSameMethod(CtMethod mainMethod, CtMethod givenMethod,
                                  MethodInjectionMode givenMethodInjectionMode) {
        return mainMethod.getSignature().equals(givenMethod.getSignature()) &&
                mainMethod.getName().equals(givenMethodInjectionMode.getMethodName());
    }

    private boolean hasSameConstructor(CtConstructor mainConstructor,
                                       CtConstructor givenConstructor) {
        return mainConstructor.getSignature()
                .equals(givenConstructor.getSignature());
    }

    private String getNormalizedParameters(String methodName, CtConstructor constructor)
            throws NotFoundException {
        return getNormalizedParameters(methodName, constructor.getParameterTypes());
    }

    private String getNormalizedParameters(String methodName, CtMethod method)
            throws NotFoundException {
        return getNormalizedParameters(methodName, method.getParameterTypes());
    }


    /**
     * Converts parameter types of given constructor to normalized form:
     * <p>
     * <code> methodName( $1, $1, .... ,$n); </code>
     */
    private String getNormalizedParameters(String methodName, CtClass[] parameterTypes) {
        StringBuilder sb = new StringBuilder();
        int size = parameterTypes.length;
        sb.append(methodName);
        sb.append("(");
        for (int param = 1; param <= size; param++) {
            sb.append(" $");
            sb.append(param);
            if (param < size) {
                sb.append(",");
            }
        }
        sb.append(")");
        sb.append(";\n");
        return sb.toString();
    }

}
