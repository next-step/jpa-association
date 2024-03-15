package database.sql.dml;

import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

// TODO: StringJoiner 너무 많다. 필요 없는 것 삭제
public class CustomSelect {
    private static final String TABLE_ALIAS = "t";
    private static final String ASSOCIATED_TABLE_ALIAS_PREFIX = "a";

    private final String tableName;
    private final List<String> allColumnNames;
    private final List<Association> associations;

    public CustomSelect(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    private CustomSelect(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.allColumnNames = entityMetadata.getAllColumnNames();
        this.associations = entityMetadata.getAssociations();
    }

    public String buildQuery(Map<String, Object> conditionMap) {
        return buildQuery() + " " + whereClause(conditionMap);
    }

    public String buildQuery() {
        StringJoiner joiner = new StringJoiner(" ")
                .add("SELECT")
                .add(selectColumns())
                .add("FROM")
                .add(tableName)
                .add(TABLE_ALIAS);
        if (!joins().isEmpty()) {
            joiner.add(joins());
        }
        return joiner.toString();
    }

    private String selectColumns() {
        StringJoiner joiner = new StringJoiner(", ");

        joiner.add(primaryTableColumns());
        for (int index = 0; index < associations.size(); index++) {
            joiner.add(associatedTableColumns(index));
        }

        return joiner.toString();
    }

    private String primaryTableColumns() {
        return allColumnNames.stream()
                .map(columnName -> columnWithAlias(columnName, TABLE_ALIAS))
                .collect(Collectors.joining(", "));
    }

    private static String columnWithAlias(String columnName, String alias) {
        return alias + "." + columnName;
    }

    private String associatedTableColumns(int index) {
        String alias = associatedTableAliasOf(index);
        Association association = associations.get(index);

        StringJoiner joiner = new StringJoiner(", ");

        joiner.add(columnWithAlias(association.getForeignKeyColumnName(), alias));
        List<String> columnNames = EntityMetadataFactory.get(association.getEntityType()).getAllColumnNames();
        for (String column : columnNames) {
            joiner.add(columnWithAlias(column, alias));
        }

        return joiner.toString();
    }

    private String joins() {
        StringJoiner joiner = new StringJoiner(" ");
        for (int index = 0; index < associations.size(); index++) {
            joiner.add(eachJoin(index));
        }
        return joiner.toString();
    }

    private String eachJoin(int index) {
        Association association = associations.get(index);
        String foreignKeyColumnName = association.getForeignKeyColumnName();

        String associatedTableAlias = associatedTableAliasOf(index);
        return new StringJoiner(" ")
                .add("LEFT JOIN")
                .add(associatedTableNameWithAlias(index, association))
                .add("ON")
                .add(columnWithAlias("id", TABLE_ALIAS))
                .add("=")
                .add(columnWithAlias(foreignKeyColumnName, associatedTableAlias))
                .toString();
    }

    private static String associatedTableNameWithAlias(int index, Association association) {
        return association.getTableName() + " " + associatedTableAliasOf(index);
    }

    private static String associatedTableAliasOf(int index) {
        return ASSOCIATED_TABLE_ALIAS_PREFIX + index;
    }

    private String whereClause(Map<String, Object> conditionMap) {
        return WhereClause.from(conditionMap, allColumnNames, TABLE_ALIAS).toQuery();
    }
}
