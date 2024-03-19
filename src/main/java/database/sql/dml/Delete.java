package database.sql.dml;

import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Delete {
    private final String tableName;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<String> allFieldNames;
    private WhereClause where;

    public Delete(String tableName, List<String> allFieldNames, PrimaryKeyEntityColumn primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.allFieldNames = allFieldNames;

        this.where = null;
    }

    public Delete where(Map<String, Object> whereMap) {
        this.where = WhereClause.from(whereMap, allFieldNames);
        return this;
    }

    public Delete id(Long id) {
        this.where(Map.of(primaryKey.getColumnName(), id));
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
