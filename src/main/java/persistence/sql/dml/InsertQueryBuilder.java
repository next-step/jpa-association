package persistence.sql.dml;

import persistence.entity.EntityBinder;
import persistence.sql.model.Column;
import persistence.sql.model.Columns;
import persistence.sql.model.Table;

import java.util.List;
import java.util.stream.Collectors;

public class InsertQueryBuilder {

    private final static String INSERT_QUERY_FORMAT = "INSERT INTO %s (%s) values (%s);";

    private final Table table;
    private final EntityBinder entityBinder;

    public InsertQueryBuilder(Table table, Object instance) {
        this.table = table;
        this.entityBinder = new EntityBinder(instance);
    }

    public String build() {
        String tableName = table.getName();
        String columnsClause = buildColumnsClause();
        String valueClause = buildColumnsValueClause();
        return String.format(INSERT_QUERY_FORMAT, tableName, columnsClause, valueClause);
    }

    private String buildColumnsClause() {
        List<String> allColumnNames = table.getAllColumnNames();
        return String.join(",", allColumnNames);
    }

    private String buildColumnsValueClause() {
        Columns columns = table.getColumns();
        return columns.stream()
                .map(this::buildColumnValueClause)
                .collect(Collectors.joining(",", "null,", ""));
    }

    private String buildColumnValueClause(Column column) {
        StringBuilder valueClauseBuilder = new StringBuilder();

        Object value = entityBinder.getValue(column);
        if (column.isType(String.class)) {
            return valueClauseBuilder.append('\'')
                    .append(value)
                    .append('\'')
                    .toString();
        }

        valueClauseBuilder.append(value);
        return valueClauseBuilder.toString();
    }
}
