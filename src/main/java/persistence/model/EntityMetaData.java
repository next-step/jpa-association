package persistence.model;

import persistence.sql.QueryException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityMetaData {

    private final String entityName;
    private final Class<?> entityClass;
    private final EntityFields entityFields;

    public EntityMetaData(final Class<?> entityClass, final EntityFields entityFields) {
        this.entityName = entityClass.getName();
        this.entityClass = entityClass;
        this.entityFields = entityFields;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Class<?> getEntityType() {
        return this.entityClass;
    }

    public List<Field> getFields() {
        return this.entityFields.getFields().stream().map(EntityField::getField).toList();
    }

    public List<EntityJoinEntityField> getJoinFields() {
        return this.entityFields.getJoinFields();
    }

    public Map<String, Object> extractValues(final Object entity) {
        if (!entity.getClass().equals(entityClass)) {
            throw new MetaDataModelMappingException("not equal class type - meta data type: " + entityName + ", parameter type: " + entity.getClass().getName());
        }

        final Map<String, Object> values = new HashMap<>();

        getFields().forEach(field -> {
            try {
                field.setAccessible(true);
                values.put(field.getName(), field.get(entity));
            } catch (IllegalAccessException e) {
                throw new QueryException("can't get field " + field.getName() + " at " + entity.getClass().getName());
            } finally {
                field.setAccessible(false);
            }
        });

        return values;
    }
}
