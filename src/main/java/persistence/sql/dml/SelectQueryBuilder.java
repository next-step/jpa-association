package persistence.sql.dml;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;
import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlConstant.SPACE;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class SelectQueryBuilder {

    private static final String SELECT_FORMAT = "%s %s";
    private static final String SELECT_DEFINITION = "SELECT";
    private static final String FROM_DEFINITION = "FROM";
    private static final String LEFT_JOIN_DEFINITION = "LEFT JOIN %s ON %s.%s = %s.%s";

    private SelectQueryBuilder() {
    }

    private static class Holder {
        static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();
    }

    public static SelectQueryBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public String generateQuery(Table table) {
        return String.format(SELECT_FORMAT, buildSelectClause(table), buildFromClause(table));
    }

    private String buildSelectClause(Table table) {
        String rootColumns = buildColumnsClause(table.getTableName(), table.getSelectColumns());
        if (table.getEagerRelationColumns().isEmpty()) {
            return SELECT_DEFINITION + SPACE.getValue() + rootColumns;
        }
        String relationColumns = COMMA.getValue() + buildRelationColumnsClause(table.getEagerRelationColumns());
        return SELECT_DEFINITION + SPACE.getValue() + rootColumns + relationColumns;
    }

    private String buildFromClause(Table table) {
        if (table.getEagerRelationColumns().isEmpty()) {
            return FROM_DEFINITION + SPACE.getValue() + table.getTableName();
        }
        String relationTable = SPACE.getValue() + buildRelationTableClause(table, table.getEagerRelationColumns());
        return FROM_DEFINITION + SPACE.getValue() + table.getTableName() + relationTable;
    }

    private String buildColumnsClause(String tableName, List<Column> columns) {
        return columns.stream()
            .map(column -> formatColumnWithName(tableName, column))
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String formatColumnWithName(String tableName, Column column) {
        return tableName + "." + column.getColumnName();
    }

    private String buildRelationColumnsClause(List<Column> columns) {
        return columns.stream()
            .filter(column -> column.getGenericType() instanceof ParameterizedType)
            .map(Column::getRelationTable)
            .map(table -> buildColumnsClause(table.getTableName(), table.getSelectColumns()))
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String buildRelationTableClause(Table root, List<Column> columns) {
        return columns.stream()
            .filter(column -> column.getGenericType() instanceof ParameterizedType)
            .map(column -> buildJoinDefinition(root, column))
            .collect(Collectors.joining()).trim();
    }

    private String buildJoinDefinition(Table root, Column column) {
        Table relationTable = column.getRelationTable();
        return String.format(LEFT_JOIN_DEFINITION, relationTable.getTableName(), root.getTableName(), root.getIdColumnName(),
            relationTable.getTableName(), column.getColumnName());
    }
}
