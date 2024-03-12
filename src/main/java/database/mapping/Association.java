package database.mapping;

import java.util.List;

public class Association {

    private final String foreignKeyColumnName;
    private final Class<?> entityType;
    private final String fieldName;

    public Association(String foreignKeyColumnName, Class<?> entityType, String fieldName) {
        this.foreignKeyColumnName = foreignKeyColumnName;
        this.entityType = entityType;
        this.fieldName = fieldName;
    }

    public String getForeignKeyColumnName() {
        return foreignKeyColumnName;
    }

    public String getTableName() {
        return getEntityMetadata().getTableName();
    }

    public List<String> getColumnNames() {
        return getEntityMetadata().getAllColumnNames();
    }

    private EntityMetadata getEntityMetadata() {
        return EntityMetadataFactory.get(entityType);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getEntityType() {
        return entityType;
    }
}
