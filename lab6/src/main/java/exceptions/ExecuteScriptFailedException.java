package exceptions;

public class ExecuteScriptFailedException extends Exception{
    public ExecuteScriptFailedException() {
    }

    public ExecuteScriptFailedException(String message) {
        super(message);
    }
}
