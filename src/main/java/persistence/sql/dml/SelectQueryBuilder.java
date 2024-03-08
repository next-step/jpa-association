package persistence.sql.dml;

import persistence.sql.column.*;

import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder implements DmlQueryBuilder {
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

    public String toStatement() {
        return selectFromJoinClause();
    }

    @Override
    public String toStatementWithId(Object id) {
        return selectFromJoinClause() + whereClause(id);
    }

    private String whereClause(Object id) {
        return String.format(WHERE_CLAUSE_FORMAT, parseTableAndColumn(tableColumn.getName(), idColumn.getName()), id);
    }

    private String getSelectFromClause() {
        return String.format(SELECT_QUERY_FORMAT, idColumn.getName(), columns.getColumnNames(),
                tableColumn.getName());
    }

    private String selectFromJoinClause() {
        List<JoinTableColumn> joinTableColumns = tableColumn.getJoinTableColumn();
        if (joinTableColumns.isEmpty() || joinTableColumns.get(0).getAssociationEntity().isLazy()) {
            return getSelectFromClause();
        }
        List<String> queries = joinTableColumns.stream()
                .filter(joinTable -> !joinTable.getAssociationEntity().isLazy())
                .map(joinTable -> getSelectFromWithAssociation(joinTable) + getJoinClauseWithAssociation(joinTable))
                .collect(Collectors.toList());

        return queries.get(0);
    }

    private String getSelectFromWithAssociation(JoinTableColumn joinTable) {
        String rootColumnsDefinition = getRootColumnDefinition();
        String joinColumnsDefinition = getJoinColumnDefinition(joinTable, joinTable.getColumns());

        return String.format(SELECT_QUERY_FORMAT,
                rootColumnsDefinition,
                joinColumnsDefinition,
                tableColumn.getName());
    }


    private String getJoinClauseWithAssociation(JoinTableColumn joinTable) {
        String fkDefinition = parseTableAndColumn(joinTable.getName(), joinTable.getAssociationEntity().getJoinColumnName());
        return String.format(JOIN_CLAUSE_FORMAT,
                joinTable.getName(),
                parseTableAndColumn(tableColumn.getName(), idColumn.getName()),
                fkDefinition
        );
    }

    private String getRootColumnDefinition() {
        String rootTableName = tableColumn.getName();

        String rootTablePkColumnDefinition = parseTableAndColumn(rootTableName, idColumn.getName());
        String rootColumnsDefinition = columns.getValues().stream()
                .filter(column -> !column.isAssociationEntity())
                .map(column -> parseTableAndColumn(rootTableName, column.getName()))
                .collect(Collectors.joining(COMMA));
        return rootTablePkColumnDefinition + COMMA + rootColumnsDefinition;
    }

    private String getJoinColumnDefinition(JoinTableColumn joinTable, Columns columns) {
        String joinTableName = joinTable.getName();
        IdColumn joinTableIdColumn = joinTable.getIdColumn();
        String fkDefinition = parseTableAndColumn(joinTableName, joinTableIdColumn.getName());

        String joinColumnsDefinition = columns.getValues().stream()
                .map(column -> parseTableAndColumn(joinTableName, column.getName()))
                .collect(Collectors.joining(COMMA));
        return fkDefinition + COMMA + joinColumnsDefinition;
    }

    private String parseTableAndColumn(String tableName, String columnName) {
        return String.format(TABLE_COLUMN_FORMAT, tableName, columnName);
    }
}
