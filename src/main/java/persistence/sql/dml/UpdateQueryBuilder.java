package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.constant.SqlConstant;
import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlConstant.DOT;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class UpdateQueryBuilder {

    private static final String UPDATE_TABLE_DEFINITION = "UPDATE %s SET %s";

    private UpdateQueryBuilder() {
    }

    private static class Holder {
        static final UpdateQueryBuilder INSTANCE = new UpdateQueryBuilder();
    }

    public static UpdateQueryBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public String generateQuery(Table table, Object object) {
        return String.format(UPDATE_TABLE_DEFINITION, table.getTableName(), valueClause(table.getTableName(), table.getUpdateColumns(), object));
    }

    private String valueClause(String tableName, List<Column> columns, Object object) {
        return columns.stream()
            .map(column -> tableName + DOT.getValue() + column.getColumnName() + SqlConstant.EQUALS.getValue() + column.getFieldValue(object))
            .collect(Collectors.joining(COMMA.getValue()));
    }
}
