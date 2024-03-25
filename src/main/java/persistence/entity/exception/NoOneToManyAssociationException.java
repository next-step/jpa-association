package persistence.entity.exception;

public class NoOneToManyAssociationException extends IllegalStateException{
    public NoOneToManyAssociationException() {
        super("OneToMany관계의 entity가 존재하지 않습니다.");
    }
}
