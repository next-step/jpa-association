package database.sql.dml;

import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Select {
    private static final String COLUMNS_DELIMITER = ", ";

    private final String tableName;
    private final List<String> allColumnNamesWithAssociations;
    private final String primaryKeyColumnName;
    private final List<String> generalEntityColumnNames;
    private WhereClause where;

    public Select(String tableName, List<String> allColumnNamesWithAssociations,
                  String primaryKeyColumnName,
                  List<String> generalEntityColumnNames
    ) {
        this.tableName = tableName;
        this.allColumnNamesWithAssociations = allColumnNamesWithAssociations;
        this.primaryKeyColumnName = primaryKeyColumnName;
        this.generalEntityColumnNames = generalEntityColumnNames;
        this.where = null;
    }

    public Select where(WhereMap whereMap) {
        this.where = WhereClause.from(whereMap, allColumnNamesWithAssociations);
        return this;
    }

    public Select id(Long id) {
        return where(WhereMap.of("id", id));
    }

    public Select ids(List<Long> ids) {
        return where(WhereMap.of("id", ids));
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
        List<String> columns = new ArrayList<>();
        columns.add(primaryKeyColumnName);
        columns.addAll(generalEntityColumnNames);

        return String.join(COLUMNS_DELIMITER, columns);
    }
}
