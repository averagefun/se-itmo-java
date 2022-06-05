package exceptions;

public class ValidateException extends Exception {
    private static final long serialVersionUID = -6648322515422223760L;
    private final String fieldName;

    public ValidateException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
