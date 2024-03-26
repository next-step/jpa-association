package persistence.entity;

import jdbc.RowMapper;
import persistence.model.AbstractEntityField;
import persistence.model.CollectionPersistentClass;
import persistence.model.EntityId;
import persistence.model.PersistentClass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CollectionEntityRowMapper<T> implements RowMapper<T> {
    private final PersistentClass<T> persistentClass;
    private final CollectionPersistentClass collectionPersistentClass;
    private final Map<Object, T> entityMap;

    public CollectionEntityRowMapper(final Class<T> clazz, final CollectionPersistentClass collectionPersistentClass) {
        if (!collectionPersistentClass.hasOwner(clazz)) {
            throw new RuntimeException("not include owner type" + clazz.getName() +" in " + collectionPersistentClass.getEntityClass().getName());
        }

        this.persistentClass = collectionPersistentClass.getOwner(clazz);
        this.collectionPersistentClass = collectionPersistentClass;
        this.entityMap = new HashMap<>();
    }

    @Override
    public T mapRow(final ResultSet resultSet) throws SQLException {
        final T entity = mapToEntity(this.persistentClass, resultSet);
        final Object joinedEntity = mapToJoinedEntity(this.collectionPersistentClass, resultSet, entity);

        setEntityAssociationFieldsValue(this.persistentClass, entity, joinedEntity);

        return entity;
    }

    private Object mapToJoinedEntity(final CollectionPersistentClass collectionPersistentClass, final ResultSet resultSet, final T entity) {
        final Object joinedEntity = collectionPersistentClass.createInstance();
        setJoinedEntityFieldsValue(joinedEntity, collectionPersistentClass, resultSet);

        return joinedEntity;
    }

    private void setEntityAssociationFieldsValue(final PersistentClass<T> persistentClass, final T entity, final Object joinedEntity) {
        persistentClass.getJoinFields()
                .forEach(joinField -> {
                    joinField.setValue(entity, joinedEntity);
                });
    }

    private T mapToEntity(final PersistentClass<T> persistentClass, final ResultSet resultSet) {
        final EntityId idField = persistentClass.getEntityFields().getIdField();
        final String tableName = persistentClass.getTableName();

        final Object idValue = extractColumnResult(resultSet, toColumnLabel(tableName, idField.getColumnName()));

        return entityMap.computeIfAbsent(idValue, key -> {
            final T instance = this.persistentClass.createInstance();
            setEntityFieldsValue(persistentClass, instance, persistentClass.getTableName(), resultSet);

            return instance;
        });
    }

    private void setEntityFieldsValue(final PersistentClass<T> persistentClass, final Object entity, final String tableName, final ResultSet resultSet) {
        persistentClass.getColumns().forEach(field -> {
            setEntityFieldValue(entity, resultSet, field, tableName);
        });
    }

    private void setJoinedEntityFieldsValue(final Object joinedEntity, final CollectionPersistentClass collectionPersistentClass, final ResultSet resultSet) {
        final String tableName = collectionPersistentClass.getTableName();
        collectionPersistentClass.getColumns().forEach(field -> {
            setEntityFieldValue(joinedEntity, resultSet, field, tableName);
        });
    }

    private void setEntityFieldValue(final Object joinedEntity, final ResultSet resultSet, final AbstractEntityField field, final String tableName) {
        final String columnLabel = toColumnLabel(tableName, field.getColumnName());
        final Object value = extractColumnResult(resultSet, columnLabel);
        field.setValue(joinedEntity, value);
    }

    private String toColumnLabel(final String tableName, final String columnName) {
        return tableName + "." + columnName;
    }

    private Object extractColumnResult(final ResultSet resultSet, final String columnLabel) {
        try {
            return resultSet.getObject(columnLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
