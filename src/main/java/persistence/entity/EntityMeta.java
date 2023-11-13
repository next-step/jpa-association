package persistence.entity;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.Query;

public class EntityMeta {
    private String methodName;
    private TableName tableName;
    private Columns columns;
    private JoinColumn joinColumn;
    private Object arg;

    public EntityMeta(TableName tableName, Columns columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public EntityMeta(String methodName, TableName tableName, Columns columns, JoinColumn joinColumn, Object arg) {
        this.methodName = methodName;
        this.tableName = tableName;
        this.columns = columns;
        this.joinColumn = joinColumn;
        this.arg = arg;
    }

    public static EntityMeta selectMeta(String methodName, TableName tableName, Columns columns, JoinColumn joinColumn, Object arg) {
        return new EntityMeta(methodName, tableName, columns, joinColumn, arg);
    }

    public String getMethodName() {
        return methodName;
    }

    public TableName getTableName() {
        return tableName;
    }

    public Columns getColumns() {
        return columns;
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public Object getArg() {
        return arg;
    }
}
