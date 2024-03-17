package database.sql.dml;

import java.util.List;

public class SelectByPrimaryKey {
    private final Select select;
    private Long id;

    public SelectByPrimaryKey(String tableName, List<String> allFieldNames) {
        select = new Select(tableName, allFieldNames);
    }

    public SelectByPrimaryKey byId(Long id) {
        this.id = id;
        return this;
    }

    public String buildQuery() {
        return select.id(this.id).buildQuery();
    }
}
