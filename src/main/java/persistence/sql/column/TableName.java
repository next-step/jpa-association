package persistence.sql.column;

import jakarta.persistence.Table;

public class TableName {
    private final ColumnNameProperty columnNameProperty;

    public TableName(String fieldName, String columnName) {
        this.columnNameProperty = new ColumnNameProperty(columnName, fieldName);
    }

    public TableName(Class<?> clazz) {
        this.columnNameProperty = new ColumnNameProperty(getColumnName(clazz), clazz.getSimpleName());
    }

    private String getColumnName(Class<?> clazz){
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getAnnotation(Table.class).name();
        }
        return clazz.getSimpleName();
    }
    public String getValue() {
        if(columnNameProperty.isColumnNameEmpty()){
            return columnNameProperty.getFieldName();
        }
        return this.columnNameProperty.getColumnName();
    }

    public String getFieldName() {
        return columnNameProperty.getFieldName();
    }
}
