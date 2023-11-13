package persistence.sql.dml;

import persistence.entity.EntityMeta;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

class UpdateQuery {
    private static final String DEFAULT_UPDATE_COLUMN_QUERY = "UPDATE %s SET %s";

    private TableName tableName;
    private Columns columns;
    private Values values;
    private Object arg;

    UpdateQuery() { }

    String get(EntityMeta entityMeta, Values values, Object arg) {
        this.tableName = entityMeta.getTableName();
        this.columns = entityMeta.getColumns();
        this.values = values;
        this.arg = arg;

        return combine();
    }

    private String combine() {
        return String.join(" ", getTableQuery(), getCondition());
    }

    private String getTableQuery() {
        return String.format(DEFAULT_UPDATE_COLUMN_QUERY, tableName.getName(), setChangeField());
    }

    private String setChangeField() {
        return values.getFieldNameAndValue(columns);
    }

    private String getCondition() {
        String condition = ConditionBuilder.getCondition(columns.getIdName(), arg, null);
        return condition.replace(" id ", " " + setConditionField("id") + " ");
    }

    private String setConditionField(String word) {
        if (word.equals("id")) {
            word = columns.getIdName();
        }
        return word;
    }
}
