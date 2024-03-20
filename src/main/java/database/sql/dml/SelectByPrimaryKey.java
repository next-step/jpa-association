package database.sql.dml;

import java.util.List;

public class SelectByPrimaryKey {
    private final Select select;
    private Long id;

    public SelectByPrimaryKey(String tableName,
                              List<String> allColumnNamesWithAssociations,
                              String primaryKeyColumnName,
                              List<String> generalEntityColumnNames) {
        select = new Select(tableName,
                            allColumnNamesWithAssociations,
                            primaryKeyColumnName,
                            generalEntityColumnNames);
    }

    public SelectByPrimaryKey byId(Long id) {
        this.id = id;
        return this;
    }

    public String buildQuery() {
        return select.id(this.id).buildQuery();
    }
}
