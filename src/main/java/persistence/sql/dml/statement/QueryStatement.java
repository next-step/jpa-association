package persistence.sql.dml.statement;

import persistence.entity.model.EntityColumn;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;

import java.util.List;
import java.util.stream.Collectors;

public class QueryStatement {
    private static final String DELIMITER = ", ";
    private static final String STRING_VALUE_FORMAT = "'%s'";

    private final StringBuilder queryBuilder;

    public QueryStatement(StringBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public static QueryStatement selectFrom(String tableName, List<String> queriedColumnNames) {
        String selectColumns = String.join(DELIMITER, queriedColumnNames);

        StringBuilder stringBuilder = new StringBuilder(String.format("select %s from %s", selectColumns, tableName));
        return new QueryStatement(stringBuilder);
    }

    public static QueryStatement selectJoin(EntityMeta entityMeta) {
        String tableName = entityMeta.getTableName();
        String tableColumns = String.join(DELIMITER, entityMeta.getColumnNames());

        EntityMeta joinEntityMeta = EntityMetaFactory.INSTANCE.create(entityMeta.getOneToManyColumnClass());
        String joinTableName = joinEntityMeta.getTableName();
        String joinTableColumns = String.join(DELIMITER, joinEntityMeta.getColumnNames());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("select %s, %s", tableColumns, joinTableColumns));
        stringBuilder.append(String.format(" from %s join %s", tableName, joinTableName));
        stringBuilder.append(String.format(" on %s.%s = ", tableName, entityMeta.getIdColumn().getName()));
        stringBuilder.append(String.format("%s.%s", joinTableName, entityMeta.getForeignKeyName()));

        return new QueryStatement(stringBuilder);
    }

    public static QueryStatement delete(String tableName) {
        StringBuilder stringBuilder = new StringBuilder(String.format("delete from %s", tableName));
        return new QueryStatement(stringBuilder);
    }

    public static QueryStatement update(String tableName) {
        StringBuilder stringBuilder = new StringBuilder(String.format("update %s", tableName));
        return new QueryStatement(stringBuilder);
    }

    public QueryStatement set(List<EntityColumn> entityColumns, Object entity) {
        String setValues = entityColumns.stream()
                .map(it -> String.format("%s = %s", it.getName(), convertColumn(it.getValue(entity))))
                .collect(Collectors.joining(", "));

        queryBuilder.append(String.format(" set %s", setValues));
        return this;
    }

    public QueryStatement where(String name, String value) {
        queryBuilder.append(" where ");
        queryBuilder.append(String.format("%s=%s", name, value));
        return this;
    }

    public QueryStatement first() {
        queryBuilder.append(" order by id desc limit 1");
        return this;
    }

    public String query() {
        return queryBuilder.toString();
    }

    private String convertColumn(Object value) {
        if (value instanceof String) {
            return String.format(STRING_VALUE_FORMAT, value);
        }

        return value.toString();
    }
}
