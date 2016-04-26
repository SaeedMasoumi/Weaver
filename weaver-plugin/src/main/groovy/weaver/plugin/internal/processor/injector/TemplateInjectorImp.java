package weaver.plugin.internal.processor.injector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import weaver.processor.injector.TemplateInjector;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class TemplateInjectorImp implements TemplateInjector {
    private ClassPool pool;

    public TemplateInjectorImp(ClassPool pool) {
        this.pool = pool;
    }

    @Override
    public void inject(Class templateClass, CtClass sourceCtClass) {
        try {
            CtClass templateCtClass = pool.get(templateClass.getCanonicalName());
            injectFields(templateCtClass, sourceCtClass);
            injectMethods(templateCtClass, sourceCtClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void injectFields(CtClass template, CtClass source) throws Exception {
        for (CtField field : template.getDeclaredFields()) {
            if (hasField(field, source)) {
                throw new Exception(String.format("Class %s already has a field named %s.",
                        source.getSimpleName(), field.getName()));
                //TODO support for existing fields
            }
            source.addField(new CtField(field, source));
        }
    }

    private boolean hasField(CtField field, CtClass clazz) {
        try {
            clazz.getField(field.getName());
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private void injectMethods(CtClass template, CtClass source) {

    }
}
