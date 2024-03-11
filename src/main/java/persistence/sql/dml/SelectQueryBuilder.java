package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlConstant.DOT;
import static persistence.sql.constant.SqlConstant.SPACE;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class SelectQueryBuilder {

    private static final String SELECT_DEFINITION = "SELECT";
    private static final String FROM_DEFINITION = "FROM";
    private JoinQueryBuilder joinQueryBuilder;

    private SelectQueryBuilder() {
        joinQueryBuilder = JoinQueryBuilder.getInstance();
    }

    private static class Holder {
        static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();
    }

    public static SelectQueryBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public String generateQuery(Table table) {
        return SPACE.concat(buildSelectClause(table), buildFromClause(table)).toString();
    }

    private String buildSelectClause(Table table) {
        StringBuilder selectClause = SPACE.concat(SELECT_DEFINITION,
            buildColumnsClause(table.getTableName(), table.getSelectColumns()));

        if (table.isEagerRelationEmpty()) {
            return selectClause.toString();
        }
        return selectClause.append(COMMA.getValue())
            .append(buildRelationColumnsClause(table.getEagerRelationColumns()))
            .toString();
    }

    private String buildFromClause(Table table) {
        StringBuilder fromClause = SPACE.concat(FROM_DEFINITION, table.getTableName());
        if (table.isEagerRelationEmpty()) {
            return fromClause.toString();
        }
        return SPACE.concat(fromClause.toString(), buildRelationTableClause(table)).toString();
    }

    private String buildColumnsClause(String tableName, List<Column> columns) {
        return columns.stream()
            .map(column -> DOT.concat(tableName, column.getColumnName()))
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String buildRelationColumnsClause(List<Column> columns) {
        return columns.stream()
            .map(Column::getRelationTable)
            .map(table -> buildColumnsClause(table.getTableName(), table.getSelectColumns()))
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String buildRelationTableClause(Table root) {
        return root.getEagerRelationTables().stream()
            .map(relationTable -> joinQueryBuilder.generateLeftJoinQuery(root, relationTable))
            .collect(Collectors.joining());
    }
}
