package persistence.sql.dml;

import persistence.sql.column.*;

import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private static final String SELECT_QUERY_FORMAT = "select %s, %s from %s";
    private static final String TABLE_COLUMN_FORMAT = "%s.%s";
    private static final String WHERE_CLAUSE_FORMAT = " where %s = %d";
    private static final String JOIN_CLAUSE_FORMAT = " join %s on %s = %s";
    private static final String COMMA = ", ";

    private TableColumn tableColumn;
    private Columns columns;
    private IdColumn idColumn;

    public SelectQueryBuilder() {
    }

    public SelectQueryBuilder build(Class<?> entity) {
        this.tableColumn = new TableColumn(entity);
        this.columns = new Columns(entity.getDeclaredFields());
        this.idColumn = new IdColumn(entity.getDeclaredFields());
        return this;
    }

    public String whereClause(Object id) {
        return String.format(WHERE_CLAUSE_FORMAT, parseTableAndColumn(tableColumn.getName(), idColumn.getName()), id);
    }

    public String selectFromWhereIdClause(Object id) {
        return selectFromClause() + whereClause(id);
    }

    public String selectFromClause() {
        return String.format(SELECT_QUERY_FORMAT, idColumn.getName(), columns.getColumnNames(),
                tableColumn.getName());
    }

    public String selectFromJoinWhereIdClause(List<JoinTableColumn> joinTableColumns, Object id) {
        return selectFromJoinClause(joinTableColumns) + whereClause(id);
    }

    public String selectFromJoinClause(List<JoinTableColumn> joinTableColumns) {
        String selectFrom = String.format(SELECT_QUERY_FORMAT, getRootColumnsDefinition(), getAssociationColumnsDefinition(joinTableColumns), tableColumn.getName());
        return selectFrom + getJoinColumn(joinTableColumns);
    }

    private String getAssociationColumnsDefinition(List<JoinTableColumn> joinTableColumns) {
        return joinTableColumns.stream()
                .map(JoinTableColumn::getColumnDefinition)
                .collect(Collectors.joining(COMMA));
    }

    private String getJoinColumn(List<JoinTableColumn> joinTableColumns) {
        return joinTableColumns.stream()
                .map(this::getJoinClauseWithAssociation)
                .collect(Collectors.joining());
    }

    private String getJoinClauseWithAssociation(JoinTableColumn joinTable) {
        String fkDefinition = parseTableAndColumn(joinTable.getName(), joinTable.getAssociationEntity().getJoinColumnName());
        return String.format(JOIN_CLAUSE_FORMAT,
                joinTable.getName(),
                parseTableAndColumn(tableColumn.getName(), idColumn.getName()),
                fkDefinition
        );
    }

    private String getRootColumnsDefinition() {
        String rootTableName = tableColumn.getName();

        String rootTablePkColumnDefinition = parseTableAndColumn(rootTableName, idColumn.getName());
        String rootColumnsDefinition = columns.getValues().stream()
                .filter(column -> !column.isAssociationEntity())
                .map(column -> parseTableAndColumn(rootTableName, column.getName()))
                .collect(Collectors.joining(COMMA));
        return rootTablePkColumnDefinition + COMMA + rootColumnsDefinition;
    }

    private String parseTableAndColumn(String tableName, String columnName) {
        return String.format(TABLE_COLUMN_FORMAT, tableName, columnName);
    }
}
