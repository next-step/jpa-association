package database.sql.dml;

import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Delete {
    private final String tableName;
    private final List<String> allColumnNames;
    private final String primaryKeyColumnName;
    private WhereClause where;

    public Delete(String tableName, String primaryKeyColumnName, List<String> allColumnNames) {
        this.tableName = tableName;
        this.primaryKeyColumnName = primaryKeyColumnName;
        this.allColumnNames = allColumnNames;

        this.where = null;
    }

    public Delete where(Map<String, Object> whereMap) {
        this.where = WhereClause.from(whereMap, allColumnNames);
        return this;
    }

    public Delete id(Long id) {
        this.where(Map.of(primaryKeyColumnName, id));
        return this;
    }

    public String buildQuery() {
        StringJoiner query = new StringJoiner(" ")
                .add("DELETE")
                .add("FROM").add(tableName);

        if (where != null) {
            String whereClause = where.toQuery();
            query.add(whereClause);
        }
        return query.toString();
    }
}
