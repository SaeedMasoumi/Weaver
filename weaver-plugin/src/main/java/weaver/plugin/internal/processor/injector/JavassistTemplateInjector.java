package weaver.plugin.internal.processor.injector;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import weaver.plugin.internal.javassist.WeaverClassPool;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@Deprecated
public class JavassistTemplateInjector {
//    private WeaverClassPool pool;
//
//    public JavassistTemplateInjector(WeaverClassPool pool) {
//        this.pool = pool;
//    }
//
//
//    public void inject(String templateClassName, String templateJavaCode, CtClass sourceClass)
//            throws Exception {
//        CtClass templateCtClass = pool.getFromJavaCode(templateClassName, templateJavaCode);
//        inject(templateCtClass, sourceClass);
//    }
//
//    public void inject(Class templateClass, CtClass sourceCtClass) throws Exception {
//        CtClass templateCtClass = pool.get(templateClass.getCanonicalName());
//        inject(templateCtClass, sourceCtClass);
//    }
//
//    private void inject(CtClass template, CtClass source)
//            throws Exception {
//        source.defrost();
//        injectInterfaces(template, source);
//        injectFields(template, source);
//        injectConstructors(template, source);
//        injectMethods(template, source);
//    }
//
//    /**
//     * Copies all implemented interfaces from template class intro source class. It will not inject
//     * same implemented interfaces.
//     *
//     * @param template Given template CtClass.
//     * @param source   Given source CtClass.
//     * @throws NotFoundException thrown by javassist.
//     */
//    private void injectInterfaces(CtClass template, CtClass source) throws NotFoundException {
//        for (CtClass interfaceClass : template.getInterfaces()) {
//            if (!hasInterface(source, interfaceClass)) {
//                source.insertInterface(interfaceClass);
//            }
//        }
//    }
//
//    /**
//     * Copies all constructors from template class into source class. If same constructor signature
//     * exists in both sides, then it will create a <code>weaver__injected__constructor($)</code>
//     * method in source class and call it at the end of constructor body.
//     * <p>
//     * Also initialization statements such as:
//     * <p>
//     * <code> boolean someValue = true; </code>
//     * <p>
//     * are moved from template constructor into injected constructor as well.
//     *
//     * @param template Given template CtClass.
//     * @param source   Given source CtClass.
//     * @throws CannotCompileException thrown by javassist.
//     * @throws NotFoundException      thrown by javassist.
//     */
//    private void injectConstructors(CtClass template, CtClass source)
//            throws CannotCompileException, NotFoundException {
//        for (CtConstructor constructorInTemplate : template.getDeclaredConstructors()) {
//            boolean constructorAdded = false;
//            String key = template.getSimpleName();
//            String methodName = "weaver$$" + key + "$$" + constructorInTemplate.getName();
//            for (CtConstructor constructorInSource : source.getDeclaredConstructors()) {
//
//                if (hasSameConstructor(constructorInSource, constructorInTemplate)) {
//                    source.addMethod(
//                            constructorInTemplate.toMethod(methodName, source));
//                    String string =
//                            getNormalizedParameters(methodName, constructorInTemplate);
//                    constructorInSource.insertAfter(string);
//                    constructorAdded = true;
//                }
//            }
//            if (!constructorAdded) {
//                source.addConstructor(
//                        CtNewConstructor.copy(constructorInTemplate, source, null));
//            }
//        }
//    }
//
//    /**
//     * Copies all fields from template class into source class.
//     *
//     * @param template Given template CtClass.
//     * @param source   Given source CtClass.
//     * @throws FieldAlreadyExistsException If a field with same name already exists in source
//     *                                     class.
//     */
//    private void injectFields(CtClass template, CtClass source) throws Exception {
//        for (CtField field : template.getDeclaredFields()) {
//            if (!hasField(field, source)) {
//                source.insertField(new CtField(field, source));
//            }
//        }
//    }
//
//    private void injectMethods(CtClass template, CtClass source)
//            throws CannotCompileException, NotFoundException {
//        for (CtMethod methodInTemplate : template.getDeclaredMethods()) {
//            MethodInjectionMode injectionMode = MethodInjectionMode.getType(methodInTemplate);
//            methodInTemplate.setName(injectionMode.getMethodName());
//            CtMethod methodInSource = findMethod(methodInTemplate, source.getDeclaredMethods());
//            //method is not included in source
//            if (methodInSource == null) {
//                CtMethod methodInParent = findMethod(methodInTemplate, source.getMethods());
//                //method is not included in source and his parent.
//                if (methodInParent == null) {
//                    methodInSource = CtNewMethod.copy(methodInTemplate, source, null);
//                    source.addMethod(methodInSource);
//                    continue;
//                }
//                //method is not override
//                else {
//                    methodInSource = CtNewMethod.delegator(methodInParent, source);
//                    source.addMethod(methodInSource);
//                }
//            }
//            //to avoid duplicate method exception
//            String key = template.getSimpleName();
//            //weaving methodInTemplate into methodInSource
//            String methodName =
//                    "weaver$$" + key + "$$" + methodInTemplate.getName() +
//                            injectionMode.getSuffix();
//            source.addMethod(CtNewMethod.copy(methodInTemplate, methodName, source, null));
//            String methodCall = getNormalizedParameters(methodName, methodInTemplate);
//            switch (injectionMode) {
//                case AT_BEGINNING:
//                    methodInSource.insertBefore(methodCall);
//                    break;
//                case BEFORE_RETURN:
//                    methodInSource.insertAfter(methodCall);
//                    break;
//                default:
//                    MethodExprEditor editor =
//                            new MethodExprEditor(injectionMode, methodCall);
//                    methodInSource.instrument(editor);
//            }
//        }
//    }
//
//
//    private CtMethod findMethod(CtMethod givenMethod, CtMethod[] methods) {
//        for (CtMethod method : methods) {
//            if (hasSameMethod(givenMethod, method)) {
//                return method;
//            }
//        }
//        return null;
//    }
//
//    private boolean hasInterface(CtClass clazz, CtClass interfaze) throws NotFoundException {
//        for (CtClass interfaceClass : clazz.getInterfaces()) {
//            if (interfaze.getName().equals(interfaceClass.getName())) return true;
//        }
//        return false;
//    }
//
//    private boolean hasField(CtField field, CtClass clazz) {
//        try {
//            clazz.getField(field.getName());
//            return true;
//        } catch (NotFoundException e) {
//            return false;
//        }
//    }
//
//    private boolean hasSameMethod(CtMethod mainMethod, CtMethod givenMethod) {
//        return mainMethod.getSignature().equals(givenMethod.getSignature()) &&
//                mainMethod.getName().equals(givenMethod.getName());
//    }
//
//    private boolean hasSameConstructor(CtConstructor mainConstructor,
//                                       CtConstructor givenConstructor) {
//        return mainConstructor.getSignature()
//                .equals(givenConstructor.getSignature());
//    }
//
//    private String getNormalizedParameters(String methodName, CtConstructor constructor)
//            throws NotFoundException {
//        return getNormalizedParameters(methodName, constructor.getParameterTypes());
//    }
//
//    private String getNormalizedParameters(String methodName, CtMethod method)
//            throws NotFoundException {
//        return getNormalizedParameters(methodName, method.getParameterTypes());
//    }
//
//
//    /**
//     * Converts parameter types of given constructor to normalized form:
//     * <p>
//     * <code> methodName( $1, $1, .... ,$n); </code>
//     */
//    private String getNormalizedParameters(String methodName, CtClass[] parameterTypes) {
//        StringBuilder sb = new StringBuilder();
//        int size = parameterTypes.length;
//        sb.append(methodName);
//        sb.append("(");
//        for (int param = 1; param <= size; param++) {
//            sb.append(" $");
//            sb.append(param);
//            if (param < size) {
//                sb.append(",");
//            }
//        }
//        sb.append(")");
//        sb.append(";\n");
//        return sb.toString();
//    }

}
