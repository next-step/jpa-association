package persistence.sql.dml;

import persistence.sql.column.*;
import persistence.sql.dialect.Dialect;

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
    private final Dialect dialect;

    public SelectQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public SelectQueryBuilder build(Class<?> entity) {
        this.tableColumn = new TableColumn(entity);
        this.columns = new Columns(entity.getDeclaredFields());
        this.idColumn = new IdColumn(entity.getDeclaredFields());
        return this;
    }

    @Override
    public String toStatementWithId(Object id) {
        return toStatement() + whereClause(id);
    }

    private String whereClause(Object id) {
        return String.format(WHERE_CLAUSE_FORMAT, parseTableAndColumn(tableColumn.getName(), idColumn.getName()), id);
    }

    public String toStatement() {
        return String.format(SELECT_QUERY_FORMAT, idColumn.getName(), columns.getColumnNames(),
                tableColumn.getName());
    }

    public String toJoinStatementWithId(Object id) {
        return toJoinStatement() + whereClause(id);
    }

    public String toJoinStatement() {
        JoinTableColumn joinTable = tableColumn.getJoinTableColumn();
        String rootColumnsDefinition = getRootColumnDefinition();
        String joinColumnsDefinition = getJoinColumnDefinition(joinTable, joinTable.getColumns());

        String selectFromCause = String.format(SELECT_QUERY_FORMAT,
                rootColumnsDefinition,
                joinColumnsDefinition,
                tableColumn.getName());

        String fkDefinition = parseTableAndColumn(joinTable.getName(), joinTable.getAssociationEntity().getJoinColumnName());
        String joinClause = String.format(JOIN_CLAUSE_FORMAT,
                joinTable.getName(),
                parseTableAndColumn(tableColumn.getName(), idColumn.getName()),
                fkDefinition
        );
        return selectFromCause + joinClause;
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
