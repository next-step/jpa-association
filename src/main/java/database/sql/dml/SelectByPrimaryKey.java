package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;

public class SelectByPrimaryKey {
    private final String tableName;
    private final String primaryKeyColumnName;
    private final String joinedAllColumnNames;

    public SelectByPrimaryKey(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    private SelectByPrimaryKey(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.primaryKeyColumnName = entityMetadata.getPrimaryKeyColumnName();
        this.joinedAllColumnNames = entityMetadata.getJoinedAllColumnNames();
    }

    public String buildQuery(Long id) {
        return String.format("SELECT %s FROM %s WHERE %s = %d",
                             joinedAllColumnNames,
                             tableName,
                             primaryKeyColumnName,
                             id);
    }
}
