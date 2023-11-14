package persistence.sql.exception;

import persistence.sql.exception.impl.ColumnNotFoundException;
import persistence.sql.exception.impl.DefaultConstructorRequiredException;
import persistence.sql.exception.impl.DuplicateCallException;
import persistence.sql.exception.impl.MappingFailedException;
import persistence.sql.exception.impl.NotSupportedTypeException;
import persistence.sql.exception.impl.PreconditionRequiredException;
import persistence.sql.exception.impl.RequiredAnnotationException;
import persistence.sql.exception.impl.UnrecognizedGeneratedValueException;

public final class ClassMappingException {

    public static ColumnNotFoundException columnNotFound(String columnName) {
        return new ColumnNotFoundException(String.format("[%s] 컬럼을(를) 찾을 수 없습니다.", columnName));
    }

    public static NotSupportedTypeException notSupportedType(Class<?> typeName) {
        return new NotSupportedTypeException(String.format("[%s] 은 지원되지 않는 타입입니다.", typeName));
    }

    public static PreconditionRequiredException preconditionRequired(String precondition) {
        return new PreconditionRequiredException(String.format("[%s] 는 선조건으로 필요합니다.", precondition));
    }

    public static DuplicateCallException duplicateCallMethod(String duplicateCall) {
        return new DuplicateCallException(String.format("[%s] 는 한번만 호출되어야 합니다.", duplicateCall));
    }

    public static RequiredAnnotationException requiredAnnotation(Class<?> type, String annotationName) {
        return new RequiredAnnotationException(String.format("[%s] 클래스에서 [%s] 어노테이션이 필요합니다.", type.getSimpleName(), annotationName));
    }

    public static DefaultConstructorRequiredException defaultConstructorRequired() {
        return new DefaultConstructorRequiredException("초기 생성자가 없습니다.");
    }

    public static UnrecognizedGeneratedValueException unrecognizedGeneratedValue(String strategyName) {
        return new UnrecognizedGeneratedValueException(String.format("[%s] 채번 전략은 사용할 수 없습니다.", strategyName));
    }

    public static MappingFailedException mappingFail(String fieldName) {
        return new MappingFailedException(String.format("[%s] 필드를 엔티티로 매핑할 수 없습니다.", fieldName));
    }
}
