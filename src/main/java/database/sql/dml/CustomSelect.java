package database.sql.dml;

import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.column.EntityColumn;
import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSelect {
    private static final String TABLE_ALIAS = "t";
    private static final String ASSOCIATED_TABLE_ALIAS_PREFIX = "a";
    private static final String SELECT = "SELECT %s FROM %s";
    private static final String QUERY_WITH_WHERE = "%s WHERE %s";
    private static final String LEFT_JOIN_CLAUSE = "LEFT JOIN %s ON %s = %s";
    private static final String COLUMNS_DELIMITER = ", ";

    private final String tableName;
    private final List<EntityColumn> allEntityColumns;
    private final List<String> allColumnNamesWithAssociations;
    private final List<Association> associations;

    public CustomSelect(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    private CustomSelect(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.allEntityColumns = entityMetadata.getAllEntityColumns();
        this.allColumnNamesWithAssociations = entityMetadata.getAllColumnNamesWithAssociations();
        this.associations = entityMetadata.getAssociations();
    }

    public String buildQuery(WhereMap whereMap) {
        return String.format(QUERY_WITH_WHERE, buildQuery(), whereClause(whereMap));
    }

    public String buildQuery() {
        String columns = String.join(COLUMNS_DELIMITER, selectColumns());
        String query = String.format(SELECT, columns, tableWithAlias(this.tableName, TABLE_ALIAS));
        if (!joins().isEmpty()) {
            query = query + " " + String.join(" ", joins());
        }
        return query;
    }

    private List<String> selectColumns() {
        List<String> columns = new LinkedList<>(primaryTableColumns());
        for (int tableIndex = 0; tableIndex < associations.size(); tableIndex++) {
            if (associations.get(tableIndex).isLazyLoad()) {
                continue;
            }
            columns.addAll(associatedTableColumns(tableIndex));
        }
        return columns;
    }

    private List<String> primaryTableColumns() {
        return allEntityColumns.stream()
                .map(entityColumn -> columnWithAlias(entityColumn.getColumnName(), TABLE_ALIAS))
                .collect(Collectors.toList());
    }

    private List<String> associatedTableColumns(int index) {
        Association association = associations.get(index);
        Class<?> genericType = association.getFieldGenericType();
        EntityMetadata entityMetadata = EntityMetadataFactory.get(genericType);
        String tableAlias = associatedTableAliasOf(index);
        List<EntityColumn> allEntityColumns = entityMetadata.getAllEntityColumns();

        String foreignKeyColumnName = association.getForeignKeyColumnName();

        List<String> columns = new ArrayList<>();
        columns.add(columnWithAlias(foreignKeyColumnName, tableAlias));
        for (EntityColumn allEntityColumn : allEntityColumns) {
            String columnName = allEntityColumn.getColumnName();
            columns.add(columnWithAlias(columnName, tableAlias));
        }
        return columns;
    }

    private List<String> joins() {
        List<String> joins = new ArrayList<>();
        for (int index = 0; index < associations.size(); index++) {
            if (associations.get(index).isLazyLoad()) {
                continue;
            }
            joins.add(eachJoin(index));
        }
        return joins;
    }

    private String eachJoin(int index) {
        Association association = associations.get(index);
        String tableName = association.getTableName();
        String tableAlias = associatedTableAliasOf(index);
        String foreignKeyColumnName = association.getForeignKeyColumnName();

        return String.format(LEFT_JOIN_CLAUSE,
                             tableWithAlias(tableName, tableAlias),
                             columnWithAlias("id", TABLE_ALIAS),
                             columnWithAlias(foreignKeyColumnName, tableAlias));
    }

    private static String columnWithAlias(String columnName, String alias) {
        return alias + "." + columnName;
    }

    private static String tableWithAlias(String tableName, String alias) {
        return tableName + " " + alias;
    }

    private static String associatedTableAliasOf(int index) {
        return ASSOCIATED_TABLE_ALIAS_PREFIX + index;
    }

    private String whereClause(WhereMap whereMap) {
        return WhereClause.from(whereMap, allColumnNamesWithAssociations, TABLE_ALIAS)
                .withWhereClause(false)
                .toQuery();
    }
}
