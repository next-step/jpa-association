package persistence.sql.common.meta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import persistence.exception.NotFoundIdException;

public class Columns {

    private final Column[] value;

    private Columns(Field[] fields) {
        this.value = Column.of(fields);
    }

    public static Columns of(Field[] fields) {
        return new Columns(fields);
    }

    /**
     * 칼럼 제약조건에 대해서 문자열로 반환합니다.
     */
    public String getConstraintsWithColumns() {
        return Arrays.stream(value)
            .map(column -> column.getName()
                + column.getType()
                + column.getConstraints().getNotNull()
                + column.getConstraints().getGeneratedValue())
            .collect(Collectors.joining(", "));
    }

    /**
     * 칼럼명을 ','으로 이어 한 문자열로 반환합니다. 예) "name, age, gender"
     */
    public String getColumnsWithComma() {
        return Arrays.stream(value)
            .map(Column::getName)
            .collect(Collectors.joining(", "));
    }

    public String getColumnsWithComma(String alias) {
        return Arrays.stream(value)
            .map(column -> alias + "." + column.getName())
            .collect(Collectors.joining(", "));
    }

    public Column getIdEntity() {
        return Arrays.stream(value)
            .filter(Column::isPrimaryKey)
            .findFirst()
            .orElseThrow(NotFoundIdException::new);
    }

    /**
     * @return
     * @Id의 field 명을 가져온다.
     */
    public String getIdName() {
        return getIdEntity().getName();
    }

    public String getIdFieldName() {
        return getIdEntity().getFieldName();
    }

    public String getPrimaryKeyWithComma() {
        return Arrays.stream(value)
            .filter(Column::isPrimaryKey)
            .map(Column::getName)
            .collect(Collectors.joining(", "));
    }

    public Column[] getValue() {
        return value;
    }
}
