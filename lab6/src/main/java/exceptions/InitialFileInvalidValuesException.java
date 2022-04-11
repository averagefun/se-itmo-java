package exceptions;

public class InitialFileInvalidValuesException extends RuntimeException {
    public InitialFileInvalidValuesException() {
    }
    public InitialFileInvalidValuesException(String message) {
        super(message);
    }
}
