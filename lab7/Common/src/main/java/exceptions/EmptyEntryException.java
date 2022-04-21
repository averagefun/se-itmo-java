package exceptions;

public class EmptyEntryException extends InvalidEntryException {
    public EmptyEntryException() {
    }
    public EmptyEntryException(String message) {
        super(message);
    }
}
