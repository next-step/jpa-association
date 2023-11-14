package persistence.sql.exception.impl;

public class UndefinedFieldException extends RuntimeException {

    public UndefinedFieldException() {
    }

    public UndefinedFieldException(String message) {
        super(message);
    }

    public UndefinedFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
