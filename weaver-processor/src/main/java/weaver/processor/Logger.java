package weaver.processor;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Logger {

    void debug(String name);

    void quiet(String name);

    void info(String name);

    void warning(String name);

}
