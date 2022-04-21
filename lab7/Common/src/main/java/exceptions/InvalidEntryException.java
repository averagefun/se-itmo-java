package exceptions;

public class InvalidEntryException extends RuntimeException {
    public InvalidEntryException() {
    }
    public InvalidEntryException(String message) {
        super(message);
    }
}
