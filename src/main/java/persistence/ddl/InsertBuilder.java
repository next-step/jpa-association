package persistence.ddl;

import persistence.ColumnMap;
import persistence.CustomTable;

public class InsertBuilder {
    private static final String KEYWORD = "insert into %s (%s) values (%s)";
    private final ColumnMap columnMap;
    private final CustomTable table;

    public InsertBuilder(CustomTable table, ColumnMap columnMap) {
        this.columnMap = columnMap;
        this.table = table;
    }

    public String query() {
        return String.format(KEYWORD,
                table.expression(),
                String.join(",", columnMap.names()),
                columnMap.values());
    }

}
