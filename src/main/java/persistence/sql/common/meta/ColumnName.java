package persistence.sql.common.meta;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import java.lang.reflect.Field;
import utils.StringUtils;

public class ColumnName {

    private final String fieldName;
    private final String name;

    private ColumnName(Field field) {
        this.fieldName = field.getName();
        this.name = extractName(field);
    }

    public static ColumnName of(Field field) {
        return new ColumnName(field);
    }

    /**
     * @Column의 name이 유효하다면 column명으로 설정하여 반환합니다.
     * 없을 경우 기존 field명을 반환합니다.
     */
    private String extractName(Field field) {
        String columnName = StringUtils.camelToSnake(field.getName());

        if (retrieveNameFromColumn(field) != null) {
            return retrieveNameFromColumn(field);
        }

        if (retrieveNameFromJoinColumn(field) != null) {
            return retrieveNameFromJoinColumn(field);
        }

        return columnName;
    }

    private String retrieveNameFromColumn(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            return null;
        }

        if ("".equals(field.getDeclaredAnnotation(Column.class).name())) {
            return null;
        }

        return field.getDeclaredAnnotation(Column.class).name();
    }

    private String retrieveNameFromJoinColumn(Field field) {
        if (!field.isAnnotationPresent(JoinColumn.class)) {
            return null;
        }

        if ("".equals(field.getDeclaredAnnotation(JoinColumn.class).name())) {
            return null;
        }

        return field.getDeclaredAnnotation(JoinColumn.class).name();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getName() {
        return name;
    }
}
