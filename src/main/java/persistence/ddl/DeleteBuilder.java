package persistence.ddl;

import persistence.CustomTable;

public class DeleteBuilder {
    private static final String KEYWORD = "DELETE FROM %s WHERE %s = %s";
    private final CustomTable table;

    public DeleteBuilder(CustomTable table) {
        this.table = table;
    }

    public String query(String column, Object value) {
        return String.format(KEYWORD, table.expression(), column, value).toUpperCase();
    }
}
