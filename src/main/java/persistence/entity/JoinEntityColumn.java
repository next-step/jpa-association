package persistence.entity;

import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;

public class JoinEntityColumn {

    private final String columnName;
    private final String fieldName;

    public JoinEntityColumn(Field field) {
        this.columnName = getJoinField(field);
        this.fieldName = field.getName();
    }

    private String getJoinField(Field joinField) {
        JoinColumn joinColumn = joinField.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null && !joinColumn.name().isEmpty()) {
            return joinColumn.name();
        }
        return joinField.getName();
    }

    public String getColumnName() {
        return columnName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
