package persistence.sql.ddl;

import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.dialect.Dialect;

public class CreateQueryBuilder implements DdlQueryBuilder {

    private static final String CREATE_TABLE_DDL = "create table %s (%s, %s)";

    private final TableColumn tableColumn;
    private final Columns columns;
    private final IdColumn idColumn;
    private final Dialect dialect;

    public CreateQueryBuilder(TableColumn tableColumn, Columns columns, IdColumn idColumn, Dialect dialect) {
        this.tableColumn = tableColumn;
        this.columns = columns;
        this.idColumn = idColumn;
        this.dialect = dialect;
    }

    @Override
    public String build() {
        return String.format(CREATE_TABLE_DDL, tableColumn.getName(), idColumn.getDefinition(dialect), columns.getColumnsDefinition(dialect));
    }
}
