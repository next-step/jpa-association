package database.sql.dml;

import database.mapping.column.EntityColumn;
import database.sql.dml.part.ValueClause;
import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static database.sql.Util.quote;

public class Update {
    private final String tableName;
    private final List<EntityColumn> generalColumns;
    private final String primaryKeyColumnName;
    private Map<String, Object> changes;
    private WhereClause where;

    public Update(String tableName, List<EntityColumn> generalColumns, EntityColumn primaryKey) {
        this.tableName = tableName;
        this.generalColumns = generalColumns;
        this.primaryKeyColumnName = primaryKey.getColumnName();
        this.changes = null;
        this.where = null;
    }

    public Update changes(Map<String, Object> changes) {
        this.changes = changes;
        return this;
    }

    public Update changes(Object entity) {
        this.changes(ValueClause.fromEntity(entity));
        return this;
    }

    public Update byId(long id) {
        this.where = WhereClause.from(Map.of(primaryKeyColumnName, id), List.of(primaryKeyColumnName));
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
        for (String columnName : namesOf(generalColumns)) {
            if (changes.containsKey(columnName)) {
                joiner.add(String.format("%s = %s", columnName, quote(changes.get(columnName))));
            }
        }
        return joiner.toString();
    }

    private List<String> namesOf(List<EntityColumn> generalColumns) {
        return generalColumns.stream().map(EntityColumn::getColumnName).collect(Collectors.toList());
    }
}
