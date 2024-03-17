package persistence.sql.dml;

import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityColumns;
import persistence.entity.metadata.EntityMetadata;

import java.util.stream.Collectors;

public class CustomSelect implements Select {

    private static final  String COLUMN_SEPARATOR = ", ";

    private String selectAll(String tableName, EntityColumns columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(createColumnNamesClause(columns));
        sql.append(" FROM ");
        sql.append(tableName);

        return sql.toString();
    }

    public String selectByIdQuery(String tableName, EntityColumns columns, Object id) {

        return selectAll(tableName, columns) + createWhereIdClause(columns, id);
    }

    @Override
    public String selectJoinQuery(EntityMetadata mainEntity, EntityMetadata joinEntity, String joinColumn, Object id) {
        StringBuilder sql = new StringBuilder();
        sql.append(selectAll(mainEntity.getTableName(), joinEntity.getColumns()));
        sql.append(" LEFT JOIN ");
        sql.append(joinEntity.getTableName());
        sql.append(" ON ");
        sql.append(getColumnName(mainEntity.getIdColumn()));
        sql.append(" = ");
        sql.append(formTableDotColumnName(joinEntity.getTableName(), joinColumn));
        sql.append(createWhereIdClause(mainEntity.getColumns(), id));

        return sql.toString();
    }

    private String getColumnName(EntityColumn entityColumn) {
        return formTableDotColumnName(entityColumn.getTableName(), entityColumn.getColumnName());
    }

    private String formTableDotColumnName(String tableName, String columnName) {
        return String.format("%s.%s", tableName, columnName);
    }


    private String createWhereIdClause(EntityColumns columns, Object value) {
        StringBuilder sql = new StringBuilder(" WHERE ");
        sql.append(createCondition(getColumnName(columns.getIdColumn()), value, "="));

        return sql.toString();
    }

    private String createColumnNamesClause(EntityColumns columns) {
        return columns.getColumns().stream()
                .map(this::getColumnName)
                .collect(Collectors.joining(COLUMN_SEPARATOR));
    }

    private String createCondition(String columnName, Object value, String operator) {

        return String.format("%s %s %s", columnName, operator, formatValue(value));
    }

    private String formatValue(Object value) {
        if (value instanceof String) {

            return "'" + value + "'";
        }

        return value == null ? "" : value.toString();
    }

}
