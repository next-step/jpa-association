package database.sql.dml.part;

import database.sql.dml.part.where.FilterExpression;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WhereClause {
    private final Map<String, Object> conditionMap;
    private final List<String> allColumnNames;
    private final String alias;

    private WhereClause(Map<String, Object> conditionMap, List<String> allColumnNames, String alias) {
        this.conditionMap = conditionMap;
        this.allColumnNames = allColumnNames;
        this.alias = alias;
    }

    public static WhereClause from(Map<String, Object> conditionMap, List<String> allColumnNames, String alias) {
        checkColumnNameInCondition(conditionMap, allColumnNames);

        return new WhereClause(conditionMap, allColumnNames, alias);
    }

    public static WhereClause from(Map<String, Object> conditionMap, List<String> allColumnNames) {
        return from(conditionMap, allColumnNames, null);
    }

    private static void checkColumnNameInCondition(Map<String, Object> conditionMap, List<String> allColumnNames) {
        for (String inputColumnName : conditionMap.keySet()) {
            if (!allColumnNames.contains(inputColumnName)) {
                throw new RuntimeException("Invalid query: " + inputColumnName);
            }
        }
    }

    public String toQuery() {
        if (conditionMap.isEmpty()) {
            return FilterExpression.EMPTY;
        }

        return allColumnNames.stream()
                .filter(conditionMap::containsKey)
                .map(columnName -> columnAndValue(columnName, conditionMap.get(columnName)))
                .collect(Collectors.joining(" AND ", "WHERE ", ""));
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
