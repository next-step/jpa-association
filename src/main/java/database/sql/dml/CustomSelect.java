package database.sql.dml;

import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.column.EntityColumn;
import database.sql.dml.part.WhereClause;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: StringJoiner 너무 많다. 필요 없는 것 삭제
public class CustomSelect {
    private static final String TABLE_ALIAS = "t";
    private static final String ASSOCIATED_TABLE_ALIAS_PREFIX = "a";
    private static final String SELECT = "SELECT %s FROM %s";
    private static final String QUERY_WITH_WHERE = "%s WHERE %s";
    private static final String LEFT_JOIN_CLAUSE = "LEFT JOIN %s ON %s = %s";
    public static final String COLUMNS_DELIMITER = ", ";

    private final String tableName;
    private final List<EntityColumn> allEntityColumns;
    private final List<Association> associations;

    public CustomSelect(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    private CustomSelect(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.allEntityColumns = entityMetadata.getAllEntityColumns();
        this.associations = entityMetadata.getAssociations();
    }

    public String buildQuery(Map<String, Object> conditionMap) {
        return String.format(QUERY_WITH_WHERE, buildQuery(), whereClause(conditionMap));
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
        List<String> columns = new LinkedList<>();
        columns.addAll(primaryTableColumns());
        for (int tableIndex = 0; tableIndex < associations.size(); tableIndex++) {
            columns.addAll(associatedTableColumns(tableIndex));
        }

        return columns;
    }

    private List<String> primaryTableColumns() {
        return allEntityColumns.stream()
                .map(entityColumn -> columnWithAlias(entityColumn.getColumnName(), TABLE_ALIAS))
                .collect(Collectors.toList());
    }

    private List<String> associatedTableColumns(int tableIndex) {
        List<String> columns = new ArrayList<>();

        Association association = associations.get(tableIndex);
        String alias = associatedTableAliasOf(tableIndex);

        columns.add(columnWithAlias(association.getForeignKeyColumnName(), alias));

        EntityMetadata entityMetadata = EntityMetadataFactory.get(association.getEntityType());
        for (EntityColumn allEntityColumn : entityMetadata.getAllEntityColumns()) {
            columns.add(columnWithAlias(allEntityColumn.getColumnName(), alias));
        }

        return columns;
    }

    private List<String> joins() {
        List<String> joins = new ArrayList<>();
        for (int index = 0; index < associations.size(); index++) {
            joins.add(eachJoin(index));
        }
        return joins;
    }

    private String eachJoin(int index) {
        Association association = associations.get(index);
        String tableName = association.getTableName();
        String tableAlias = associatedTableAliasOf(index);
        return String.format(LEFT_JOIN_CLAUSE,
                             tableWithAlias(tableName, tableAlias),
                             columnWithAlias("id", TABLE_ALIAS),
                             columnWithAlias(association.getForeignKeyColumnName(), tableAlias));
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

    private String whereClause(Map<String, Object> conditionMap) {
        return WhereClause.from(conditionMap, allEntityColumns, TABLE_ALIAS)
                .withWhereClause(false)
                .toQuery();
    }
}
