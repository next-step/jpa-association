package hibernate.dml;

import hibernate.entity.meta.column.EntityColumn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectQueryBuilder {

    public static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();

    private static final String SELECT_ALL_QUERY = "select %s from %s;";
    private static final String SELECT_QUERY = "select %s from %s where %s = %s;";

    private static final String SELECT_QUERY_COLUMN_DELIMITER = ", ";

    private SelectQueryBuilder() {
    }

    public String generateQuery(
            final String tableName,
            final List<String> fieldNames,
            final EntityColumn entityId,
            final Object id
    ) {
        return String.format(SELECT_QUERY, parseColumnQueries(fieldNames), tableName, entityId.getFieldName(), id);
    }

    public String generateAllQuery(final String tableName, final List<String> fieldNames) {
        return String.format(SELECT_ALL_QUERY, parseColumnQueries(fieldNames), tableName);
    }


    public String generateQuery(
            final String tableName,
            final List<String> fieldNames,
            final EntityColumn entityId,
            final Object id,
            final Map<String, List<String>> joinTableFields,
            final Map<String, Object> joinTableIds
    ) {
        return generateDefaultQuery(tableName, fieldNames, entityId, joinTableFields, joinTableIds)
                .append(" ")
                .append("where ")
                .append(parseColumnQuery(tableName, entityId.getFieldName()))
                .append(" = ")
                .append(id)
                .append(";")
                .toString();
    }

    public String generateAllQuery(
            final String tableName,
            final List<String> fieldNames,
            final EntityColumn entityId,
            final Map<String, List<String>> joinTableFields,
            final Map<String, Object> joinTableIds
    ) {
        return generateDefaultQuery(tableName, fieldNames, entityId, joinTableFields, joinTableIds)
                .append(";")
                .toString();
    }

    private StringBuilder generateDefaultQuery(String tableName, List<String> fieldNames, EntityColumn entityId, Map<String, List<String>> joinTableFields, Map<String, Object> joinTableIds) {
        final List<String> parsedFieldNames = fieldNames.stream()
                .map(fieldName -> parseColumnQuery(tableName, fieldName))
                .collect(Collectors.toList());

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select ")
                .append(parseColumnQueries(parsedFieldNames));
        if (!joinTableFields.isEmpty()) {
            queryBuilder.append(", ")
                    .append(parseJoinColumnQueries(joinTableFields))
                    .append(" ");
        } else {
            queryBuilder.append(" ");
        }
        queryBuilder.append("from ")
                .append(tableName)
                .append(" ");
        if (!joinTableFields.isEmpty()) {
            queryBuilder.append(parseJoinTableQuery(tableName, entityId, joinTableIds));
        }
        return queryBuilder;
    }

    private String parseColumnQueries(final List<String> fieldNames) {
        return String.join(SELECT_QUERY_COLUMN_DELIMITER, fieldNames);
    }

    private String parseColumnQueries(final String tableName, List<String> fieldNames) {
        return fieldNames.stream()
                .map(fieldName -> parseColumnQuery(tableName, fieldName))
                .collect(Collectors.joining(SELECT_QUERY_COLUMN_DELIMITER));
    }

    private String parseColumnQuery(final String tableName, String fieldName) {
        return tableName + "." + fieldName;
    }

    private String parseJoinColumnQueries(final Map<String, List<String>> joinTableFields) {
        return joinTableFields.entrySet()
                .stream()
                .map(entry -> parseColumnQueries(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(SELECT_QUERY_COLUMN_DELIMITER));
    }

    private String parseJoinTableQuery(final String tableName, final EntityColumn entityId, final Map<String, Object> joinTableIds) {
        return joinTableIds.entrySet()
                .stream()
                .map(entry -> "join " + entry.getKey() + " on " + parseColumnQuery(tableName, entityId.getFieldName()) + " = " + parseColumnQuery(entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.joining(" "));
    }
}
