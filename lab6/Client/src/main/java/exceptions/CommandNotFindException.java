package exceptions;

public class CommandNotFindException extends RuntimeException{
    public CommandNotFindException() {
    }
    public CommandNotFindException(String message) {
        super(message);
    }
}
