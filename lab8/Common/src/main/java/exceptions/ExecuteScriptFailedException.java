package exceptions;

public class ExecuteScriptFailedException extends Exception{
    private static final long serialVersionUID = -1997644547303249624L;

    public ExecuteScriptFailedException() {
    }

    public ExecuteScriptFailedException(String message) {
        super(message);
    }
}
