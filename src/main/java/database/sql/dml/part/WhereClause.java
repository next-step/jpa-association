package database.sql.dml.part;

import database.mapping.column.EntityColumn;
import database.sql.dml.part.where.FilterExpression;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WhereClause {
    private final Map<String, Object> conditionMap;
    private final List<String> allFieldNames;
    private final String alias;
    private boolean withWhereClause;

    private WhereClause(Map<String, Object> conditionMap, List<String> allFieldNames, String alias) {
        this.conditionMap = conditionMap;
        this.allFieldNames = allFieldNames;
        this.alias = alias;
        this.withWhereClause = true;
    }

    public static WhereClause from(Map<String, Object> conditionMap, List<String> allFieldNames) {
        return from(conditionMap, allFieldNames, null);
    }

    public static WhereClause from(Map<String, Object> conditionMap, List<String> allFieldNames, String alias) {
        checkColumnNameInCondition(conditionMap, allFieldNames);

        return new WhereClause(conditionMap, allFieldNames, alias);
    }

    private static void checkColumnNameInCondition(Map<String, Object> conditionMap, List<String> allFieldNames) {
        for (String inputColumnName : conditionMap.keySet()) {
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
        if (conditionMap.isEmpty()) {
            return FilterExpression.EMPTY;
        }

        String prefix = withWhereClause ? "WHERE " : "";
        return allFieldNames.stream()
                .filter(conditionMap::containsKey)
                .map(columnName -> columnAndValue(columnName, conditionMap.get(columnName)))
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
