package persistence.sql.exception;

import persistence.sql.exception.impl.AccessRequiredException;
import persistence.sql.exception.impl.UndefinedFieldException;

public final class FieldException {

    public static AccessRequiredException accessRequire(String fieldName) {
        return new AccessRequiredException(String.format("[%s] 필드를 수정할 수 없습니다.", fieldName));
    }

    public static UndefinedFieldException undefinedField(String fieldName) {
        return new UndefinedFieldException(String.format("%s 필드는 정의되지 않은 필드입니다.", fieldName));
    }
}
