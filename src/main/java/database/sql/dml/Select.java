package database.sql.dml;

import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Select {
    private final String tableName;
    private final List<String> allColumnNames;
    private WhereClause where;

    public Select(String tableName, List<String> allColumnNames) {
        this.tableName = tableName;
        this.allColumnNames = allColumnNames;
        this.where = null;
    }

    public Select where(Map<String, Object> whereMap) {
        this.where = WhereClause.from(whereMap, allColumnNames);
        return this;
    }

    public Select id(Long id) {
        return this.where(Map.of("id", id));
    }

    public String buildQuery() {
        StringJoiner query = new StringJoiner(" ")
                .add("SELECT")
                .add(getJoinedAllColumnNames())
                .add("FROM").add(tableName);

        if (where != null) {
            String whereClause = where.toQuery();
            query.add(whereClause);
        }

        return query.toString();
    }

    private String getJoinedAllColumnNames() {
        return String.join(", ", allColumnNames);
    }
}
