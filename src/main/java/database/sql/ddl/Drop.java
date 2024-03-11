package database.sql.ddl;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;

public class Drop {
    private final String tableName;

    public Drop(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
    }

    public Drop(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    public String buildQuery() {
        return String.format("DROP TABLE %s", tableName);
    }
}
