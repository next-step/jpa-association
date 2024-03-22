package persistence.model;

import persistence.sql.QueryException;
import persistence.sql.mapping.TableBinder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMetaData {

    private final String entityName;
    private final String tableName;
    private final Class<?> entityClass;
    private final EntityFields entityFields;
    private final EntityJoinEntityFields entityJoinEntityFields;

    public EntityMetaData(final Class<?> entityClass) {
        this.entityName = entityClass.getName();
        this.tableName = TableBinder.toTableName(entityClass);
        this.entityClass = entityClass;
        this.entityFields = new EntityFields();
        this.entityJoinEntityFields = new EntityJoinEntityFields();
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public void addEntityField(final EntityField field) {
        this.entityFields.addField(field);
    }

    public void addEntityJoinEntityField(final EntityJoinEntityField entityJoinEntityField) {
        this.entityJoinEntityFields.addJoinField(entityJoinEntityField);
    }

    public Field getIdField() {
        return this.entityFields.getIdField().getField();
    }

    public boolean isSameTableName(final String tableName) {
        return tableName.equalsIgnoreCase(this.tableName);
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Class<?> getEntityType() {
        return this.entityClass;
    }

    public List<Field> getFields() {
        return this.entityFields.getFields().stream().map(EntityField::getField).collect(Collectors.toList());
    }

    public List<EntityJoinEntityField> getJoinFields() {
        return this.entityJoinEntityFields.getJoinFields();
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
