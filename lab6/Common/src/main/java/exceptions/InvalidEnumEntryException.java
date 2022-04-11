package exceptions;

public class InvalidEnumEntryException extends InvalidEntryException {
    public InvalidEnumEntryException() {
    }
    public InvalidEnumEntryException(String message) {
        super(message);
    }
}
