package persistence.sql.dml;

import persistence.entity.EntityMeta;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

class DeleteQuery {

    private static final String DEFAULT_DELETE_QUERY = "DELETE FROM %s";

    private TableName tableName;
    private Columns columns;
    private Object arg;

    DeleteQuery() { }

    String get(EntityMeta entityMeta, Object arg) {
        this.tableName = entityMeta.getTableName();
        this.columns = entityMeta.getColumns();
        this.arg = arg;

        return combine();
    }

    private String combine() {
        return String.join(" ", getTableQuery(), getCondition());
    }

    private String getTableQuery() {
        return String.format(DEFAULT_DELETE_QUERY, tableName.getName());
    }

    private String getCondition() {
        return ConditionBuilder.getCondition(columns.getIdName(), arg, null);
    }
}
