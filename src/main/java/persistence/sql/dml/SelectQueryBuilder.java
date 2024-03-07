package persistence.sql.dml;

import persistence.sql.meta.AssociationTable;
import persistence.sql.meta.Column;
import persistence.sql.meta.DataType;
import persistence.sql.meta.IdColumn;
import persistence.sql.meta.Table;

import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private static final String SELECT_QUERY_TEMPLATE = "SELECT %s FROM %s";
    private static final String JOIN_QUERY_TEMPLATE = " LEFT JOIN %s ON %s = %s";
    private static final String WHERE_CLAUSE_TEMPLATE = " WHERE %s = %s";
    private static final String COLUMN_DELIMITER = ", ";

    private static class InstanceHolder {
        private static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();
    }

    public static SelectQueryBuilder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public String build(Table table, Object id) {
        String baseQuery = createBaseQuery(table);
        String whereClause = createWhereQuery(table, id);

        return baseQuery + whereClause;
    }

    public String buildWithJoin(Table table, Object id) {
        String baseQuery = createBaseQueryForJoin(table);
        String joinQuery = createJoinQuery(table);
        String whereClause = createWhereQuery(table, id);
        return baseQuery + joinQuery + whereClause;
    }

    private String createBaseQuery(Table table) {
        String columns = getColumnsNames(table.getName(), table.getColumns());
        return String.format(SELECT_QUERY_TEMPLATE, columns, table.getName());
    }

    private String createBaseQueryForJoin(Table table) {
        String columns = getColumnsNameWithAssociation(table);
        return String.format(SELECT_QUERY_TEMPLATE, columns, table.getName());
    }

    private String getColumnsNameWithAssociation(Table table) {
        return getColumnsNames(table.getName(), table.getColumns()) + getAssociationColumns(table);
    }

    private String getAssociationColumns(Table table) {
        List<AssociationTable> associationTables = table.getAssociationTables();
        StringBuilder associationColumns = new StringBuilder();
        for (AssociationTable associationTable : associationTables) {
            associationColumns.append(COLUMN_DELIMITER);
            associationColumns.append(getColumnsNames(associationTable.getName(), associationTable.getColumns()));
        }
        return associationColumns.toString();
    }

    private String getColumnsNames(String tableName, List<Column> columns) {
        return columns.stream()
                .map(column -> getQualifiedColumnName(tableName, column.getName()))
                .collect(Collectors.joining(COLUMN_DELIMITER));
    }

    private String createJoinQuery(Table table) {
        StringBuilder joinQuery = new StringBuilder();
        List<AssociationTable> associationTables = table.getAssociationTables();
        for (AssociationTable associationTable : associationTables) {
            String tableIdName = getQualifiedColumnName(table.getName(), table.getIdColumn().getName());
            String joinTableName = getQualifiedColumnName(associationTable.getName(), associationTable.getJoinColumn());
            joinQuery.append(String.format(JOIN_QUERY_TEMPLATE, associationTable.getName(), tableIdName, joinTableName));
        }
        return joinQuery.toString();
    }

    private String createWhereQuery(Table table, Object id) {
        IdColumn idColumn = table.getIdColumn();
        String value = getDmlValue(id, idColumn);
        return String.format(WHERE_CLAUSE_TEMPLATE, getQualifiedColumnName(table.getName(), idColumn.getName()), value);
    }

    private String getDmlValue(Object id, Column column) {
        DataType columnType = column.getType();
        if (columnType.isVarchar()) {
            return String.format("'%s'", id);
        }
        return id.toString();
    }

    private String getQualifiedColumnName(String tableName, String columnName) {
        return tableName + "." + columnName;
    }
}
