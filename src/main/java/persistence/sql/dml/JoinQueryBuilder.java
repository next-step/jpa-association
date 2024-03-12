package persistence.sql.dml;

import persistence.sql.model.Table;

import java.util.stream.Collectors;

public class JoinQueryBuilder {

    private final static String JOIN_QUERY_FORMAT = "JOIN %s ON %s=%s";

    private final Table table;

    public JoinQueryBuilder(Table table) {
        this.table = table;
    }

    public String build() {
        String tableName = table.getName();
        String pkColumnName = tableName + '.' + table.getPKColumnName();

        return table.getJoinColumns()
                .stream()
                .map(joinColumn -> {
                    Table joinTable = joinColumn.getTable();
                    String joinTableName = joinTable.getName();
                    String joinColumnName = joinTableName + "." + joinColumn.getName();
                    return String.format(JOIN_QUERY_FORMAT, joinTableName, pkColumnName, joinColumnName);
                })
                .collect(Collectors.joining(" "));
    }
}
