package persistence.sql.exception.impl;

public class DefaultConstructorRequiredException extends RuntimeException {

    public DefaultConstructorRequiredException() {
    }

    public DefaultConstructorRequiredException(String message) {
        super(message);
    }

    public DefaultConstructorRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
