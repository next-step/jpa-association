package persistence.sql.entity.exception;

public class InvalidProxyException extends RuntimeException{
    private static final String MESSAGE = "유효하지 프록시입니다.";

    public InvalidProxyException() {
        super(MESSAGE);
    }
}
