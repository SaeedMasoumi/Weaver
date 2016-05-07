package weaver.toolkit;

import javassist.CtClass;
import javassist.CtField;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class FieldWeaver extends BytecodeWeaver<ClassWeaver> {

    private int modifier = 0;
    private String fieldQualifiedName;
    private String fieldName = "weaver";
    private String initializer = "";

    FieldWeaver(ClassWeaver classWeaver) {
        super(classWeaver);
    }

    public FieldWeaver modifiers(int... modifiers) {
        for (int m : modifiers) this.modifier |= m;
        return this;
    }

    public FieldWeaver type(String qualifiedName) {
        fieldQualifiedName = qualifiedName;
        return this;
    }

    public FieldWeaver name(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public FieldWeaver instantiateIt() {
        this.initializer = "new " + fieldQualifiedName + "()";
        return this;
    }

    @Override
    protected void weaving() throws Exception {
        CtClass fieldType = getPool().get(fieldQualifiedName);
        CtClass ctClass = getCtClass();
        CtField field = new CtField(fieldType, fieldName, ctClass);
        field.setModifiers(modifier);
        if (initializer.isEmpty()) {
            ctClass.addField(field);
        } else {
            ctClass.addField(field, initializer);
        }
    }

}
