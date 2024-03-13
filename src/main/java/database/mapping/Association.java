package database.mapping;

import database.dialect.Dialect;

import java.lang.reflect.Type;

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

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public String getTableName() {
        return getOwnerEntityMetadata().getTableName();
    }

    private EntityMetadata getOwnerEntityMetadata() {
        return EntityMetadataFactory.get(entityType);
    }

    public String toColumnDefinition(Dialect dialect) {
        return foreignKeyColumnName + " " + getForeignKeyColumnType(dialect) + " NOT NULL";
    }

    private String getForeignKeyColumnType(Dialect dialect) {
        Type foreignKeyColumnType = getOwnerEntityMetadata().getPrimaryKey().getFieldType();

        return dialect.convertToSqlTypeDefinition((Class<?>) foreignKeyColumnType, 0);
    }
}
