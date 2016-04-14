package weaver.processor;

/**
 * The {@code WeaverProcessor} can use facilities provided by this interface during processing.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface ProcessingEnvironment {
    /**
     * @return Returns the logger used to report info, warnings, and other notices.
     */
    Logger getLogger();
}
