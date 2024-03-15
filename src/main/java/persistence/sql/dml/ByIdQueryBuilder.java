package persistence.sql.dml;

import persistence.entity.EntityId;
import persistence.sql.model.Table;

public class ByIdQueryBuilder {

    private final static String BY_ID_QUERY_FORMAT = "%s.%s=%s";

    private final Table table;
    private final EntityId id;

    public ByIdQueryBuilder(Table table, EntityId id) {
        this.table = table;
        this.id = id;
    }

    public String build() {
        String tableName = table.getName();
        String pkColumnName = table.getPKColumnName();
        return String.format(BY_ID_QUERY_FORMAT, tableName, pkColumnName, id);
    }
}
