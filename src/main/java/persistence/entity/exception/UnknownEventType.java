package persistence.entity.exception;

public class UnknownEventType extends RuntimeException {

    public UnknownEventType() {
    }

    public UnknownEventType(String message) {
        super(message);
    }

    public UnknownEventType(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownEventType(Throwable cause) {
        super(cause);
    }

    public UnknownEventType(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
