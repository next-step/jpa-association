package persistence.sql.column;

import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;

public class JoinEntityColumn {

    private final ColumnNameProperty columnNameProperty;

    public JoinEntityColumn(Field field) {
        this.columnNameProperty = new ColumnNameProperty(getJoinField(field), field.getName());
    }

    private String getJoinField(Field joinField) {
        JoinColumn joinColumn = joinField.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null && !joinColumn.name().isEmpty()) {
            return joinColumn.name();
        }
        return joinField.getName();
    }

    public String getColumnName() {
        return columnNameProperty.getColumnName();
    }

    public String getFieldName() {
        return columnNameProperty.getFieldName();
    }

    public String parseTableAndColumn(String tableName) {
        return tableName + "." + columnNameProperty.getColumnName();

    }
}
