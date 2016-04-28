package weaver.plugin.internal.exception

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class FieldAlreadyExistsException extends Exception {

    FieldAlreadyExistsException() {
    }

    FieldAlreadyExistsException(String var1) {
        super(var1)
    }

    FieldAlreadyExistsException(String var1, Throwable var2) {
        super(var1, var2)
    }

    FieldAlreadyExistsException(Throwable var1) {
        super(var1)
    }

    FieldAlreadyExistsException(String var1, Throwable var2, boolean var3, boolean var4) {
        super(var1, var2, var3, var4)
    }
}
