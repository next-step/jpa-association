package persistence.sql.component;

import persistence.sql.NameUtils;

import java.lang.reflect.Field;

public class ColumnInfo {
    private TableInfo tableInfo;
    private Field columnField;

    private ColumnInfo(TableInfo tableInfo, Field field) {
        this.tableInfo = tableInfo;
        this.columnField = field;
    }

    public static ColumnInfo of(TableInfo tableInfo, Field columnField) {
        return new ColumnInfo(tableInfo, columnField);
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public Field getColumnField() {
        return columnField;
    }

    public String getFullName() {
        return tableInfo.getTableName() + "." + NameUtils.getColumnName(columnField);
    }
}
