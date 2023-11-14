package persistence.sql.exception.impl;

public class DuplicateCallException extends RuntimeException {

    public DuplicateCallException() {
    }

    public DuplicateCallException(String message) {
        super(message);
    }

    public DuplicateCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
