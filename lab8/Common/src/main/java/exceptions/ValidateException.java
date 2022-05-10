package exceptions;

public class ValidateException extends Exception {
    private final String fieldName;

    public ValidateException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
