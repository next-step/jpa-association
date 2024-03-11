package persistence.sql.dml;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static persistence.sql.constant.SqlConstant.COMMA;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class InsertQueryBuilder {

    private static final String INSERT_DEFINITION = "INSERT INTO %s (%s) VALUES (%s)";

    private InsertQueryBuilder() {
    }
    private static class Holder {
        static final InsertQueryBuilder INSTANCE = new InsertQueryBuilder();
    }

    public static InsertQueryBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public String generateQuery(Table table, Object entity) {
        return String.format(INSERT_DEFINITION, table.getTableName(),
            columnsClause(table.getInsertColumns()), valueClause(table.getInsertColumns(), entity));
    }

    public String generateQuery(Table table, Object entity, Object parent) {

        StringBuilder columnsBuilder = new StringBuilder(columnsClause(table.getInsertColumns()));
        StringBuilder valuesBuilder = new StringBuilder(valueClause(table.getInsertColumns(), entity));

        String relationColumns = relationColumnsClause(Table.getRelationColumns(table), parent);
        String relationValues = relationValueClause(parent);

        if (!relationColumns.isEmpty()) {
            columnsBuilder.append(COMMA.getValue())
                .append(relationColumns);

            valuesBuilder.append(COMMA.getValue())
                .append(relationValues);
        }

        return String.format(INSERT_DEFINITION, table.getTableName(), columnsBuilder, valuesBuilder);
    }


    private String columnsClause(List<Column> columns) {
        return columns.stream()
            .map(Column::getColumnName)
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String relationColumnsClause(Set<Map.Entry<Table, Column>> relationColumns, Object parent) {
        return relationColumns.stream()
            .filter(entry -> entry.getKey().equals(Table.getInstance(parent.getClass())))
            .map(entry -> entry.getValue().getColumnName())
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String valueClause(List<Column> columns, Object entity) {
        return columns.stream()
            .map(column -> column.getFieldValue(entity))
            .map(String::valueOf)
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String relationValueClause(Object parent) {
        return Table.getInstance(parent.getClass())
            .getIdValue(parent)
            .toString();
    }
}
