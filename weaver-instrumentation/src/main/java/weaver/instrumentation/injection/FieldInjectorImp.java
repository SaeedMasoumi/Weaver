package weaver.instrumentation.injection;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import weaver.common.injection.ClassInjector;
import weaver.common.injection.FieldInjector;

import static weaver.instrumentation.internal.JavassistUtils.getModifiers;
import static weaver.instrumentation.internal.StringUtils.capitalize;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class FieldInjectorImp extends BaseInjector<ClassInjectorImp>
        implements FieldInjector<ClassInjector> {

    private int modifiers = 0;
    private String fieldTypeQualifiedName;
    private String fieldName;
    private boolean instantiateIt = false;
    private boolean addSetter = false;
    private boolean addGetter = false;
    private String instantiateValue = "";

    FieldInjectorImp(ClassInjectorImp classInjectorImp, String type, String fieldName) {
        super(classInjectorImp);
        this.fieldTypeQualifiedName = type;
        this.fieldName = fieldName;
    }

    @Override
    public FieldInjector addModifiers(int... modifiers) {
        this.modifiers = getModifiers(this.modifiers, modifiers);
        return this;
    }

    @Override
    public FieldInjector addSetter() {
        addSetter = true;
        return this;
    }

    @Override
    public FieldInjector addGetter() {
        addGetter = true;
        return this;
    }

    @Override
    public FieldInjector initializeIt() {
        this.instantiateIt = true;
        return this;
    }

    @Override
    public FieldInjector withInitializer(String value) {
        this.instantiateValue = value;
        return this;
    }

    @Override
    public ClassInjector inject() throws Exception {
        CtClass fieldType = getPool().get(fieldTypeQualifiedName);
        CtClass ctClass = getCtClass();
        CtField field = new CtField(fieldType, fieldName, ctClass);
        field.setModifiers(modifiers);
        if (!instantiateValue.isEmpty()) {
            ctClass.addField(field, instantiateValue);
        } else if (instantiateIt) {
            ctClass.addField(field, "new " + fieldTypeQualifiedName + "()");
        } else {
            ctClass.addField(field);
        }
        if (addSetter) {
            ctClass.addMethod(CtNewMethod.setter("set" + capitalize(fieldName), field));
        }
        if (addGetter) {
            ctClass.addMethod(CtNewMethod.getter("get" + capitalize(fieldName), field));
        }
        return parent;
    }

}
