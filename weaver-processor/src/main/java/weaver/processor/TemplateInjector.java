package weaver.processor;

import javassist.CtClass;

/**
 * <code>TemplateInjector</code> allows you to inject all declared interfaces, fields, constructors
 * and methods from one class(template class) to another class(destination class).
 * <p>
 * You can pass your template classes as text or {@link Class}. Passing your template class as text
 * format, gives you some more features: <ul> <li> You can use 3rd party libraries like <a
 * href="https://github.com/square/javapoet">JavaPoet</a> to generate a java code as text
 * representation. </li> <li> If you want to access to an object in your source code that has some
 * dependencies to another libraries, you will not need to add those dependencies to your
 * processor's classpath. </li> </ul>
 * <p>
 * So, how injection works?
 * <p>
 * Interface Injection: Your template class can implement an interface, then if your destination
 * class not implemented it yet, Weaver will implement it in your destination class.
 * <p>
 * Field Injection: Your template class can declare a field, then if your destination class didn't
 * have that field, Weaver will declare your field.
 * <p>
 * Constructor injection: Your template class can declare a constructor, if your destination class
 * has same signature with your declared constructor, then weaver will invoke your declared
 * constructor after destination class constructor otherwise it will add a new constructor to your
 * destination class.
 * <p>
 * Method injection: Methods are different, you can inject a method in different statements of your
 * destination class. For example if you have <code>void foo();</code> method in your destination
 * class, you can declare a method with the same name in your template class, then weaver will
 * inject it at the end of the body (before return).  But if you want to inject it at the beginning
 * of the method body then change your method name to <code>void foo$$AtBeginning()</code>.
 * <p>
 * Allowed suffixes for method names are listed below: <ul> <li> <code>methodName$$AtBeginning</code>,
 * add your method at the beginning of the body. </li> <li> <code>methodName$$BeforeReturn</code>
 * (default behaviour, same as <code>methodname</code> without suffix), add your method at the end
 * of the body. </li> </li> <li> <code>methodName$$BeforeSuper</code>, add your method before supper
 * call. </li> <li> <code>methodName$$AfterSuper</code>, add your method after supper call. </li>
 * </ul>
 * <p>
 * If your destination class didn't have same signature of given method in your template class, then
 * weaver will add a new method.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface TemplateInjector {

    /**
     * Injects a text code into a given ctClass.
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
