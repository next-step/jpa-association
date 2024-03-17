package database.sql.dml.part;

import database.sql.dml.part.where.FilterExpression;

import java.util.List;
import java.util.stream.Collectors;

public class WhereClause {
    private final WhereMap whereMap;
    private final List<String> allFieldNames;
    private final String alias;
    private boolean withWhereClause;

    private WhereClause(WhereMap whereMap, List<String> allFieldNames, String alias) {
        this.whereMap = whereMap;
        this.allFieldNames = allFieldNames;
        this.alias = alias;
        this.withWhereClause = true;
    }

    public static WhereClause from(WhereMap whereMap, List<String> allFieldNames) {
        return from(whereMap, allFieldNames, null);
    }

    public static WhereClause from(WhereMap whereMap, List<String> allFieldNames, String alias) {
        checkColumnNameInCondition(whereMap, allFieldNames);

        return new WhereClause(whereMap, allFieldNames, alias);
    }

    private static void checkColumnNameInCondition(WhereMap whereMap, List<String> allFieldNames) {
        for (String inputColumnName : whereMap.keySet()) {
            if (!allFieldNames.contains(inputColumnName)) {
                throw new RuntimeException("Invalid query: " + inputColumnName);
            }
        }
    }

    public WhereClause withWhereClause(boolean bool) {
        this.withWhereClause = bool;
        return this;
    }

    public String toQuery() {
        if (whereMap.isEmpty()) {
            return FilterExpression.EMPTY;
        }

        String prefix = withWhereClause ? "WHERE " : "";
        return allFieldNames.stream()
                .filter(whereMap::containsKey)
                .map(columnName -> columnAndValue(columnName, whereMap.get(columnName)))
                .collect(Collectors.joining(" AND ", prefix, ""));
    }

    private String columnAndValue(String columnName, Object value) {
        String columnNameWithAlias = columnNameWithAlias(columnName);
        FilterExpression expr = FilterExpression.from(columnNameWithAlias, value);
        return expr.toQuery();
    }

    private String columnNameWithAlias(String columnName) {
        if (alias != null)
            return alias + "." + columnName;

        return columnName;
    }
}
