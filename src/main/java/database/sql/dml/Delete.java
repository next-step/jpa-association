package database.sql.dml;

import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;

import java.util.List;
import java.util.StringJoiner;

public class Delete {
    private final String tableName;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<String> allColumnNamesWithAssociations;
    private WhereClause where;

    public Delete(String tableName, List<String> allColumnNamesWithAssociations, PrimaryKeyEntityColumn primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.allColumnNamesWithAssociations = allColumnNamesWithAssociations;

        this.where = null;
    }

    public Delete where(WhereMap whereMap) {
        this.where = WhereClause.from(whereMap, allColumnNamesWithAssociations);
        return this;
    }

    public Delete id(Long id) {
        return this.where(WhereMap.of(primaryKey.getColumnName(), id));
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
