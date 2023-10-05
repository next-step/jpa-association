package persistence.exception;

public class ChildClassNotFoundException extends RuntimeException {
    public ChildClassNotFoundException(ClassNotFoundException e) {
        super(e);
    }
}
