package persistence.sql.type;

import jakarta.persistence.Table;

public class TableName {
    private final String fieldName;
    private final String columnName;

    public TableName(String fieldName, String columnName) {
        this.fieldName = fieldName;
        this.columnName = columnName;
    }

    public TableName(Class<?> clazz) {
        this.fieldName = clazz.getSimpleName();
        this.columnName = getColumnName(clazz);
    }

    private String getColumnName(Class<?> clazz){
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getAnnotation(Table.class).name();
        }
        return clazz.getSimpleName();
    }
    public String getValue() {
        if (this.columnName == null || this.columnName.isBlank() || this.columnName.isEmpty()) {
            return this.fieldName;
        }
        return this.columnName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
