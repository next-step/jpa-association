package persistence.sql.dml;

import persistence.entity.common.EntityId;
import persistence.sql.model.BaseTable;
import persistence.sql.model.JoinColumn;
import persistence.sql.model.Table;

import java.util.stream.Collectors;

public class FindQueryBuilder {

    private final static String Find_QUERY_FORMAT = "SELECT %s FROM %s";
    private final static String WHERE_FORMAT = "WHERE %s";

    private final Table table;
    private final JoinQueryBuilder joinQueryBuilder;

    public FindQueryBuilder(Table table) {
        this.table = table;
        this.joinQueryBuilder = new JoinQueryBuilder(table);
    }

    public String build() {
        String columnNames = buildColumnNames(table);
        String tableName = table.getName();

        if (!table.hasJoinColumn()) {
            return String.format(Find_QUERY_FORMAT, columnNames, tableName);
        }

        String joinColumnNames = buildJoinColumnNames();
        String columnClause = String.join(",", columnNames, joinColumnNames);

        String findQuery = String.format(Find_QUERY_FORMAT, columnClause, tableName);
        String joinClause = joinQueryBuilder.build();
        return String.join(" ", findQuery, joinClause);
    }

    public String buildById(EntityId id) {
        ByIdQueryBuilder byIdQueryBuilder = new ByIdQueryBuilder(table, id);

        String columnNames = buildColumnNames(table);
        String tableName = table.getName();
        String byIdQuery = byIdQueryBuilder.build();
        String whereClause = String.format(WHERE_FORMAT, byIdQuery);

        if (!table.hasJoinColumn()) {
            String findQuery = String.format(Find_QUERY_FORMAT, columnNames, tableName);
            return String.join(" ", findQuery, whereClause);
        }

        String joinColumnNames = buildJoinColumnNames();
        String columnClause = String.join(",", columnNames, joinColumnNames);

        String findQuery = String.format(Find_QUERY_FORMAT, columnClause, tableName);
        String joinClause = joinQueryBuilder.build();
        return String.join(" ", findQuery, joinClause, whereClause);
    }

    private String buildJoinColumnNames() {
        return table.getJoinColumns()
                .stream()
                .map(JoinColumn::getTable)
                .map(this::buildColumnNames)
                .collect(Collectors.joining(","));
    }

    private String buildColumnNames(BaseTable table) {
        String tableName = table.getName();
        return table.getAllColumnNames()
                .stream()
                .collect(Collectors.joining("," + tableName + ".", tableName + ".", ""));
    }
}
