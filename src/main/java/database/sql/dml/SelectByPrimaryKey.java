package database.sql.dml;

import database.mapping.column.EntityColumn;

import java.util.List;

public class SelectByPrimaryKey {
    private final Select select;
    private Long id;

    public SelectByPrimaryKey(String tableName, List<EntityColumn> allEntityColumns) {
        select = new Select(tableName, allEntityColumns);
    }

    public SelectByPrimaryKey byId(Long id) {
        this.id = id;
        return this;
    }

    public String buildQuery() {
        return select.id(this.id).buildQuery();
    }
}
