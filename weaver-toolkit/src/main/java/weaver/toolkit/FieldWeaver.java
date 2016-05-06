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

    public FieldWeaver modifier(int modifier) {
        this.modifier = modifier;
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


    public FieldWeaver newInstance() {
        this.initializer = "new " + fieldQualifiedName + "()";
        return this;
    }

    @Override
    protected void weaving() throws Exception {
        CtClass fieldType = getPool().get(fieldQualifiedName);
        CtClass ctClass = getCtClass();
        CtField field = new CtField(fieldType, fieldName, ctClass);
        field.setModifiers(modifier);
        ctClass.addField(field, initializer);
    }

}
