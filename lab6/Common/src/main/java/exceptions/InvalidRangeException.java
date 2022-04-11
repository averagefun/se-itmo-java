package exceptions;

public class InvalidRangeException extends InvalidEntryException {
    public InvalidRangeException() {
    }
    public InvalidRangeException(String message) {
        super(message);
    }
}
