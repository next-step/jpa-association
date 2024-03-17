package database.sql.dml;

import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Select {
    private static final String COLUMNS_DELIMITER = ", ";

    private final String tableName;
    private final List<String> allFieldNames;
    private WhereClause where;

    public Select(String tableName, List<String> allFieldNames) {
        this.tableName = tableName;
        this.allFieldNames = allFieldNames;
        this.where = null;
    }

    public Select where(Map<String, Object> whereMap) {
        this.where = WhereClause.from(whereMap, allFieldNames);
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
        return String.join(COLUMNS_DELIMITER, allFieldNames);
    }
}
