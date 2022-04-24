package exceptions;

public class CommandInterruptedException extends RuntimeException {
    public CommandInterruptedException() {
    }

    public CommandInterruptedException(String message) {
        super(message);
    }
}
