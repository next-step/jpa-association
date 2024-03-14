package persistence.sql.dml;

import persistence.sql.column.*;

public class SelectQueryBuilder {
    private static final String SELECT_QUERY_FORMAT = "select %s, %s from %s";
    private static final String WHERE_CLAUSE_FORMAT = " where %s = %d";
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

    public String whereClause(Column joinColumn, String joinTableName, Object id) {
        return String.format(WHERE_CLAUSE_FORMAT, joinColumn.getTableAndColumnDefinition(joinTableName), id);
    }

    public String whereClause(Object id) {
        return String.format(WHERE_CLAUSE_FORMAT, idColumn.getTableAndColumnDefinition(tableColumn.getName()), id);
    }

    public String selectFromWhereIdClause(Column joinColumn, String joinTableName, Object id) {
        return selectFromClause() + whereClause(joinColumn, joinTableName, id);
    }

    public String selectFromWhereIdClause(Object id) {
        return selectFromClause() + whereClause(id);
    }

    public String selectFromClause() {
        return String.format(SELECT_QUERY_FORMAT, idColumn.getName(), columns.getColumnNames(),
                tableColumn.getName());
    }

    public String selectFromJoinWhereIdClause(JoinTableColumns joinTableColumns, Object id) {
        return selectFromJoinClause(joinTableColumns) + whereClause(id);
    }

    public String selectFromJoinClause(JoinTableColumns joinTableColumns) {
        String rootTableName = tableColumn.getName();
        String selectFrom = String.format(SELECT_QUERY_FORMAT, getRootColumnsDefinition(rootTableName), joinTableColumns.getAssociationColumnsDefinition(), tableColumn.getName());
        return selectFrom + joinTableColumns.getJoinDefinition(rootTableName);
    }

    private String getRootColumnsDefinition(String rootTableName) {
        String rootTablePkColumnDefinition = idColumn.getTableAndColumnDefinition(rootTableName);
        String rootColumnsDefinition = columns.getTableAndColumnDefinition(rootTableName);

        return rootTablePkColumnDefinition + COMMA + rootColumnsDefinition;
    }

}
