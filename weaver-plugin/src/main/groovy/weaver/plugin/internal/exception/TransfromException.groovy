package weaver.plugin.internal.exception

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TransfromException extends Exception{
    TransfromException() {
    }

    TransfromException(String var1) {
        super(var1)
    }

    TransfromException(String var1, Throwable var2) {
        super(var1, var2)
    }

    TransfromException(Throwable var1) {
        super(var1)
    }

    TransfromException(String var1, Throwable var2, boolean var3, boolean var4) {
        super(var1, var2, var3, var4)
    }
}
