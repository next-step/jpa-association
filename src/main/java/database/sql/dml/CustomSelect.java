package database.sql.dml;

import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CustomSelect {
    private static final String SELF_ALIAS = "t";
    private static final String TABLE_ALIAS_PREFIX = "a";

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
        StringJoiner joiner = new StringJoiner(" ");
        return joiner.add("SELECT")
                .add(columns())
                .add("FROM")
                .add(tableName)
                .add(SELF_ALIAS)
                .add(joins())
                .toString();
    }

    private String columns() {
        StringJoiner joiner = new StringJoiner(", ");

        joiner.add(primaryTableColumns());

        for (int i = 0; i < associations.size(); i++) {
            joiner.add(associatedTableColumns(i));
        }

        return joiner.toString();
    }

    private String primaryTableColumns() {
        return allColumnNames.stream()
                .map(columnName -> withAlias(SELF_ALIAS, columnName))
                .collect(Collectors.joining(", "));
    }

    private static String withAlias(String alias, String columnName) {
        return alias + "." + columnName;
    }

    private String associatedTableColumns(int i) {
        StringJoiner joiner = new StringJoiner(", ");

        String alias = tableAliasOf(i);
        Association association = associations.get(i);

        joiner.add(withAlias(alias, association.getForeignKeyColumnName()));

        for (String column : association.getColumnNames()) {
            joiner.add(withAlias(alias, column));
        }
        return joiner.toString();
    }

    private String joins() {
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < associations.size(); i++) {
            eachJoin(i, joiner);
        }
        return joiner.toString();
    }

    private void eachJoin(int i, StringJoiner joiner) {
        Association association = associations.get(i);
        String foreignKeyColumnName = association.getForeignKeyColumnName();
        String alias = tableAliasOf(i);

        joiner.add("LEFT JOIN")
                .add(association.getTableName()).add(alias)
                .add("ON")
                .add(withAlias(SELF_ALIAS, "id"))
                .add("=")
                .add(withAlias(alias, foreignKeyColumnName));
    }

    private static String tableAliasOf(int i) {
        return TABLE_ALIAS_PREFIX + i;
    }

    private String whereClause(Map<String, Object> conditionMap) {
        return WhereClause.from(conditionMap, allColumnNames, SELF_ALIAS).toQuery();
    }
}
