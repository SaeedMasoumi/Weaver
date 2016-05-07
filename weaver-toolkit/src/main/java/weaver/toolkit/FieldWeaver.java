package weaver.toolkit;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;

import static weaver.toolkit.internal.StringUtils.capitalize;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class FieldWeaver extends BytecodeWeaver<ClassWeaver> {

    private int modifier = 0;
    private String fieldQualifiedName;
    private String fieldName = "weaver";
    private boolean instantiateIt = false;
    private boolean addSetter = false;
    private boolean addGetter = false;
    private String instantiateValue = "";

    FieldWeaver(ClassWeaver classWeaver, String name) {
        super(classWeaver);
        this.fieldName = name;
    }

    public FieldWeaver modifiers(int... modifiers) {
        for (int m : modifiers) this.modifier |= m;
        return this;
    }

    public FieldWeaver type(String qualifiedName) {
        fieldQualifiedName = qualifiedName;
        return this;
    }


    public FieldWeaver addSetter() {
        addSetter = true;
        return this;
    }

    public FieldWeaver addGetter() {
        addGetter = true;
        return this;
    }

    public FieldWeaver instantiateIt() {
        this.instantiateIt = true;
        return this;
    }

    public FieldWeaver instantiate(String value) {
        this.instantiateValue = value;
        return this;
    }

    @Override
    protected void weaving() throws Exception {
        CtClass fieldType = getPool().get(fieldQualifiedName);
        CtClass ctClass = getCtClass();
        CtField field = new CtField(fieldType, fieldName, ctClass);
        field.setModifiers(modifier);
        if (!instantiateValue.isEmpty()) {
            ctClass.addField(field, instantiateValue);
        } else if (instantiateIt) {
            ctClass.addField(field, "new " + fieldQualifiedName + "()");
        } else {
            ctClass.addField(field);
        }
        if (addSetter) {
            ctClass.addMethod(CtNewMethod.setter("set" + capitalize(fieldName), field));
        }
        if (addGetter) {
            ctClass.addMethod(CtNewMethod.getter("get" + capitalize(fieldName), field));
        }
    }

}
