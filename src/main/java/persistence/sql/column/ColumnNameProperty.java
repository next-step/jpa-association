package persistence.sql.column;

public class ColumnNameProperty {
    private final String columnName;
    private final String fieldName;

    public ColumnNameProperty(String columnName, String fieldName) {
        this.columnName = columnName;
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isColumnNameEmpty() {
        return this.columnName == null || this.columnName.isBlank() || this.columnName.isEmpty();
    }
}
