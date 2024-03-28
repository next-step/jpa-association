package persistence.entity;

import persistence.model.AbstractEntityField;
import persistence.model.CollectionPersistentClass;
import persistence.model.EntityId;
import persistence.model.PersistentClass;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class CollectionEntityRowMapper<T> implements EntityRowMapper<T> {
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
    public T mapRow(final ResultSet resultSet) {
        final T entity = mapToEntity(this.persistentClass, resultSet);
        final Object joinedEntity = mapToJoinedEntity(this.collectionPersistentClass, resultSet);

        setEntityAssociationFieldsValue(this.persistentClass, entity, joinedEntity);

        return entity;
    }

    private Object mapToJoinedEntity(final CollectionPersistentClass collectionPersistentClass, final ResultSet resultSet) {
        final Object joinedEntity = collectionPersistentClass.createInstance();
        setJoinedEntityFieldsValue(joinedEntity, collectionPersistentClass, resultSet);

        return joinedEntity;
    }

    private void setEntityAssociationFieldsValue(final PersistentClass<T> persistentClass, final T entity, final Object joinedEntity) {
        persistentClass.getFields()
                .stream().filter(AbstractEntityField::isJoinField)
                .forEach(joinField -> joinField.setValue(entity, joinedEntity));
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

    private void setJoinedEntityFieldsValue(final Object joinedEntity, final CollectionPersistentClass collectionPersistentClass, final ResultSet resultSet) {
        final String tableName = collectionPersistentClass.getTableName();
        collectionPersistentClass.getColumns().forEach(field -> setEntityFieldValue(joinedEntity, resultSet, field, tableName));
    }
}
