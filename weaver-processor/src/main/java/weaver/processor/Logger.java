package weaver.processor;

/**
 * The <code>Logger</code> is used to report info, warnings, and other notices.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Logger {

    /**
     * To see your debug messages run your gradle task with -d option.
     *
     * @param message the log message.
     */
    void debug(String message);


    /**
     * To see your quiet messages run your gradle task with -q option.
     *
     * @param message the log message.
     */
    void quiet(String message);

    /**
     * To see your info messages run your gradle task with -i option.
     *
     * @param message the log message.
     */
    void info(String message);

    /**
     * Log a message at the WARN level.
     *
     * @param message the log message.
     */
    void warning(String message);

}
