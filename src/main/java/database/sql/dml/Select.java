package database.sql.dml;

import database.mapping.column.EntityColumn;
import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Select {
    private final String tableName;
    private final List<EntityColumn> allEntityColumns;
    private WhereClause where;

    public Select(String tableName, List<EntityColumn> allEntityColumns) {
        this.tableName = tableName;
        this.allEntityColumns = allEntityColumns;
        this.where = null;
    }

    public Select where(Map<String, Object> whereMap) {
        this.where = WhereClause.from(whereMap, allEntityColumns);
        return this;
    }

    public Select id(Long id) {
        return this.where(Map.of("id", id));
    }

    public String buildQuery() {
        StringJoiner query = new StringJoiner(" ")
                .add("SELECT")
                .add(joinAllColumnNames())
                .add("FROM").add(tableName);

        if (where != null) {
            String whereClause = where.toQuery();
            query.add(whereClause);
        }

        return query.toString();
    }

    private String joinAllColumnNames() {
        return allEntityColumns.stream().map(EntityColumn::getColumnName).collect(Collectors.joining(", "));
    }
}
