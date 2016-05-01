package weaver.plugin.internal.processor.injector;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtNewConstructor;
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
        injectConstructors(template, source);
        injectFields(template, source);
        injectMethods(template, source);
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
            boolean copiedConstructor = false;
            String methodName = "weaver__injected__constructor";
            for (CtConstructor constructorInSource : source.getDeclaredConstructors()) {
                if (constructorInSource.getSignature()
                        .equals(constructorInTemplate.getSignature())) {
                    source.addMethod(
                            constructorInTemplate.toMethod(methodName, source));
                    String string =
                            methodName +
                                    getNormalizedParameters(constructorInTemplate) +
                                    ";\n";
                    constructorInSource.insertAfter(string);
                    copiedConstructor = true;
                }
            }
            if (!copiedConstructor) {
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
            if (hasField(field, source)) {
                throw new FieldAlreadyExistsException(
                        String.format("Class %s already has a field named %s.",
                                source.getSimpleName(), field.getName()));
                //TODO support for existing fields
            }
            source.addField(new CtField(field, source));
        }
    }

    private void injectMethods(CtClass template, CtClass source) {

    }

    private boolean hasField(CtField field, CtClass clazz) {
        try {
            clazz.getField(field.getName());
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * Converts parameter types of given constructor to normalized form:
     * <p>
     * <code> ( $1, $1, .... ,$n) </code>
     */
    private String getNormalizedParameters(CtConstructor constructor) throws NotFoundException {
        StringBuilder sb = new StringBuilder();
        int size = constructor.getParameterTypes().length;
        sb.append("(");
        for (int param = 1; param <= size; param++) {
            sb.append(" $");
            sb.append(param);
            if (param < size) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
