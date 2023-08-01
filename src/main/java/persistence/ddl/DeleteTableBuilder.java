package persistence.ddl;

import persistence.CustomTable;

public class DeleteTableBuilder {
    private static final String KEYWORD = "drop table if exists %s cascade";
    private final CustomTable table;

    public DeleteTableBuilder(CustomTable table) {
        this.table = table;
    }

    public String query() {
        return String.format(KEYWORD, table.expression());
    }

}
