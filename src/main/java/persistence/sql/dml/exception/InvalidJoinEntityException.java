package persistence.sql.dml.exception;

public class InvalidJoinEntityException extends RuntimeException {

    private static final String MESSAGE = "서브 엔티티값 가져오는 과정에 에러가 발생하였습니다.";

    public InvalidJoinEntityException() {
        super(MESSAGE);
    }
}
