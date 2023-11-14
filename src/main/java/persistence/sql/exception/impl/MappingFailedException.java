package persistence.sql.exception.impl;

public class MappingFailedException extends RuntimeException {

    public MappingFailedException() {
    }

    public MappingFailedException(String message) {
        super(message);
    }

    public MappingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
