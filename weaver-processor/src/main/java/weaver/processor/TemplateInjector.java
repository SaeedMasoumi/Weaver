package weaver.processor;

import javassist.CtClass;

/**
 * <code>TemplateInjector</code> allows you to inject all declared interfaces, fields,
 * constructors and methods from one class to another class.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface TemplateInjector {

    /**
     * Injects a text code into a given ctClass.
     * <p>
     * Also you can use 3rd party libraries like <a href="https://github.com/square/javapoet">JavaPoet</a></a>
     * to generate a java code as text representation.
     * <p>
     * For example, If we have this template class:
     * <pre>
     * {@code
     *  String templateJavaCode = "package mypackage;\n" +
     *                            "public class MyClass{\n" +
     *                            "private int number = 0;
     *                            "public void foo() {\n" +
     *                            "System.out.println(number);\n" +
     *                            "}\n" +
     *                            "}\n";
     *   String templateClassName = "MyClass
     * }
     *
     * </pre>
     * And this source code:
     * <pre>
     * {@code
     *  public class MyClass{
     *            public void bar(){
     *                   //do something
     *             }
     *       }
     *  }
     * </pre>
     * Then by calling <code>inject(templateClassName, templateJavaCode, yourClass)</code>,
     * <code>MyClass</code> will transformed to:
     * <pre>
     * {@code
     *  public class MyClass{
     *            private int number = 0;
     *            public void bar(){
     *                   //do something
     *             }
     *            public void foo(){
     *                System.out.println(number);
     *            }
     *       }
     *  }
     * </pre>
     *
     * @param templateClassName Class name of your template text code.
     * @param templateJavaCode  Your template class as text.
     * @param sourceClass       Given source class.
     */
    void inject(String templateClassName, String templateJavaCode, CtClass sourceClass);

    /**
     * It's similar to {@link #inject(String, String, CtClass)}, but instead of passing a text code,
     * you can pass a {@link Class} file.
     *
     * @param templateClass Given template class.
     * @param sourceClass   Given source class.
     */
    void inject(Class templateClass, CtClass sourceClass);
}
