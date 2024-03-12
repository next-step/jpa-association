package persistence.sql.dml;

import persistence.sql.model.JoinTable;
import persistence.sql.model.Table;

public class JoinQueryBuilder {

    private final static String JOIN_QUERY_FORMAT = "JOIN %s ON %s=%s";

    private final Table table;

    private final JoinTable joinTable;

    public JoinQueryBuilder(Table table, JoinTable joinTable) {
        this.table = table;
        this.joinTable = joinTable;
    }

    public String build() {
        String tableName = table.getName();
        String pkColumnName = tableName + '.' + table.getPKColumnName();

        String joinTableName = joinTable.getName();
        String onClause = buildOnClause();

        return String.format(JOIN_QUERY_FORMAT, joinTableName, pkColumnName, onClause);
    }

    private String buildOnClause() {
        String tableName = joinTable.getName();
        String joinColumnName = joinTable.getJoinColumnName();
        return String.join(".", tableName, joinColumnName);
    }
}
