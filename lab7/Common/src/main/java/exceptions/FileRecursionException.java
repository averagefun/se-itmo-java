package exceptions;

public class FileRecursionException extends ExecuteScriptFailedException {
    public FileRecursionException() {
    }

    public FileRecursionException(String message) {
        super(message);
    }
}
