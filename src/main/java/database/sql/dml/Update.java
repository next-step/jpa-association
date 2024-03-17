package database.sql.dml;

import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.ValueClause;
import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static database.sql.Util.quote;

public class Update {
    private final String tableName;
    private final List<GeneralEntityColumn> generalColumns;
    private final PrimaryKeyEntityColumn primaryKey;
    private Map<String, Object> changes;
    private WhereClause where;

    public Update(String tableName, List<GeneralEntityColumn> generalColumns, PrimaryKeyEntityColumn primaryKey) {
        this.tableName = tableName;
        this.generalColumns = generalColumns;
        this.primaryKey = primaryKey;
        this.changes = null;
        this.where = null;
    }

    public Update changes(Map<String, Object> changes) {
        this.changes = changes;
        return this;
    }

    public Update changes(Object entity) {
        this.changes(ValueClause.fromEntity(entity, generalColumns));
        return this;
    }

    public Update byId(long id) {
        this.where = WhereClause.from(Map.of(primaryKey.getColumnName(), id), List.of(primaryKey.getColumnName()));
        return this;
    }

    public String buildQuery() {
        return String.format("UPDATE %s SET %s %s",
                             tableName,
                             setClauses(),
                             where.toQuery());
    }

    private String setClauses() {
        StringJoiner joiner = new StringJoiner(", ");
        for (GeneralEntityColumn generalColumn : generalColumns) {
            String columnName = generalColumn.getColumnName();

            if (changes.containsKey(columnName)) {
                joiner.add(String.format("%s = %s", columnName, quote(changes.get(columnName))));
            }
        }
        return joiner.toString();
    }
}
