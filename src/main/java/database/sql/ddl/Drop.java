package database.sql.ddl;

import database.mapping.EntityMetadata;

public class Drop {
    private final String tableName;

    public Drop(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
    }

    public Drop(Class<?> clazz) {
        this(EntityMetadata.fromClass(clazz));
    }

    public String buildQuery() {
        return String.format("DROP TABLE %s", tableName);
    }
}
