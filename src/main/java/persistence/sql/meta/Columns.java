package persistence.sql.meta;

import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Columns {

    private static final String ID_NOT_FOUND_MESSAGE =  "Id 필드가 존재하지 않습니다.";
    private static final String REQUIRED_ID_MESSAGE =  "Id 필드는 필수로 1개를 가져야 합니다.";
    private final List<Column> columns;

    public Columns(List<Column> columns) {
        this.columns = columns;
    }

    public static Columns from(Field[] fields) {
        List<Column> columnList = Arrays.stream(fields)
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .map(Column::from)
            .collect(Collectors.toList());
        validate(columnList);

        return new Columns(columnList);
    }

    private static void validate(List<Column> columns) {
        long idCount = columns.stream()
            .filter(Column::isIdAnnotation)
            .count();

        if (idCount != 1) {
            throw new IllegalArgumentException(REQUIRED_ID_MESSAGE);
        }
    }

    public Column getIdColumn() {
        return columns.stream()
            .filter(Column::isIdAnnotation)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(ID_NOT_FOUND_MESSAGE));
    }

    public Object getIdValue(Object entity) {
        return getIdColumn().getFieldValue(entity);
    }

    public List<Column> getInsertColumns() {
        return columns.stream()
            .filter(column -> !column.isGeneratedValueAnnotation() && !column.isRelationColumn())
            .collect(Collectors.toList());
    }

    public List<Column> getUpdateColumns() {
        return columns.stream()
            .filter(column -> !column.isIdAnnotation())
            .collect(Collectors.toList());
    }

    public List<Column> getSelectColumns() {
        return columns.stream()
            .filter(column -> !column.isEagerRelationColumn())
            .collect(Collectors.toList());
    }

    public List<Column> getEagerRelationColumns() {
        return columns.stream()
            .filter(Column::isEagerRelationColumn)
            .collect(Collectors.toList());
    }

    public List<Column> getRelationColumns(){
        return columns.stream()
            .filter(Column::isRelationColumn)
            .collect(Collectors.toList());
    }

    public List<Object> getRelationValues(Object entity) {
        return getRelationColumns().stream()
            .map(column -> column.getFieldValue(entity))
            .collect(Collectors.toList());
    }
}
