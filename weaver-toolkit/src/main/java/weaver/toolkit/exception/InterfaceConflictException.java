package weaver.toolkit.exception;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class InterfaceConflictException extends Exception {
    public InterfaceConflictException() {
    }

    public InterfaceConflictException(String message) {
        super(message);
    }

    public InterfaceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterfaceConflictException(Throwable cause) {
        super(cause);
    }

    public InterfaceConflictException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
