package database.sql.dml;

import java.util.List;

public class SelectByPrimaryKey {
    private final Select select;
    private Long id;

    public SelectByPrimaryKey(String tableName, List<String> allColumnNames) {
        select = new Select(tableName, allColumnNames);
    }

    public SelectByPrimaryKey byId(Long id) {
        this.id = id;
        return this;
    }

    public String buildQuery() {
        return select.id(this.id).buildQuery();
    }
}
