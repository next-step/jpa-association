package persistence.sql.dml;

import persistence.entity.EntityId;
import persistence.sql.model.BaseTable;
import persistence.sql.model.JoinTable;
import persistence.sql.model.Table;

import java.util.List;
import java.util.stream.Collectors;

public class FindQueryBuilder {

    private final static String FIND_ALL_QUERY_FORMAT = "SELECT %s FROM %s";
    private final static String FIND_BY_ID_QUERY_FORMAT = "SELECT %s FROM %s WHERE %s";

    private final Table table;

    public FindQueryBuilder(Table table) {
        this.table = table;
    }

    public String build() {
        String columnsClause = buildColumnsClause();
        String tableName = table.getName();

        String findAllQuery = String.format(FIND_ALL_QUERY_FORMAT, columnsClause, tableName);
        String joinClause = buildJoinClause();

        if (joinClause.isEmpty()) {
            return findAllQuery;
        }
        return String.join(" ", findAllQuery, joinClause);
    }

    public String buildById(EntityId id) {
        ByIdQueryBuilder byIdQueryBuilder = new ByIdQueryBuilder(table, id);

        String columnsClause = buildColumnsClause();
        String tableName = table.getName();
        String whereClause = byIdQueryBuilder.build();

        String findByIdQuery = String.format(FIND_BY_ID_QUERY_FORMAT, columnsClause, tableName, whereClause);
        String joinClause = buildJoinClause();

        if (joinClause.isEmpty()) {
            return findByIdQuery;
        }
        return String.join(" ", findByIdQuery, joinClause);
    }

    private String buildColumnsClause() {
        String columnNames = buildColumnNames(table);
        String joinColumnNames = table.getJoinTables()
                .stream()
                .map(this::buildColumnNames)
                .collect(Collectors.joining(","));

        if (joinColumnNames.isEmpty()) {
            return columnNames;
        }

        return String.join(",", columnNames, joinColumnNames);
    }

    private String buildColumnNames(BaseTable table) {
        String tableName = table.getName();
        return table.getAllColumnNames()
                .stream()
                .collect(Collectors.joining("," + tableName + ".", tableName + ".", ""));
    }

    private String buildJoinClause() {
        List<JoinTable> joinTables = table.getJoinTables();
        return joinTables.stream()
                .map(joinTable -> {
                    JoinQueryBuilder joinQueryBuilder = new JoinQueryBuilder(table, joinTable);
                    return joinQueryBuilder.build();
                })
                .collect(Collectors.joining(" "));
    }
}
