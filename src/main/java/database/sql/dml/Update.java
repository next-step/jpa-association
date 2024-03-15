package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.column.EntityColumn;
import database.sql.dml.part.ValueClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static database.sql.Util.quote;

public class Update {
    private final String tableName;
    private final List<EntityColumn> generalColumns;

    public Update(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    private Update(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.generalColumns = entityMetadata.getGeneralColumns();
    }

    public String buildQuery(long id, Map<String, Object> changes) {
        return String.format("UPDATE %s SET %s WHERE %s",
                             tableName,
                             setClauses(changes),
                             whereClauses(id));
    }

    public String buildQueryByEntity(long id, Object entity) {
        return buildQuery(id, ValueClause.fromEntity(entity));
    }

    private String setClauses(Map<String, Object> changes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (EntityColumn generalColumn : generalColumns) {
            String key = generalColumn.getColumnName();
            if (changes.containsKey(key)) {
                joiner.add(String.format("%s = %s", key, quote(changes.get(key))));
            }
        }
        return joiner.toString();
    }

    private String whereClauses(long id) {
        return String.format("id = %d", id);
    }
}
