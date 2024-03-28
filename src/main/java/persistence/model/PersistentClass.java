package persistence.model;

import jakarta.persistence.Entity;
import persistence.ReflectionUtils;
import persistence.sql.QueryException;
import persistence.sql.mapping.TableBinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistentClass<T> {

    private final Class<T> clazz;
    private final EntityFields fields;
    private final String entityName;
    private final String tableName;

    public PersistentClass(final Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new QueryException(clazz.getSimpleName() + " is not entity");
        }
        this.clazz = clazz;
        this.fields = new EntityFields();
        this.entityName = this.clazz.getName();
        this.tableName = TableBinder.toTableName(this.clazz);
    }

    public Class<T> getEntityClass() {
        return this.clazz;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void addEntityFields(final List<AbstractEntityField> entityFields) {
        this.fields.addFields(entityFields);
    }

    public EntityFields getEntityFields() {
        return this.fields;
    }

    public List<AbstractEntityField> getFields() {
        return this.fields.getFields();
    }

    public List<AbstractEntityField> getColumns() {
        return this.fields.getColumns();
    }

    public T createInstance() {
        return ReflectionUtils.createInstance(clazz);
    }

    public Map<String, Object> extractValues(final Object entity) {
        if (!entity.getClass().equals(this.clazz)) {
            throw new MetaDataModelMappingException("not equal class type - meta data type: " + entityName + ", parameter type: " + entity.getClass().getName());
        }

        final Map<String, Object> values = new HashMap<>();

        getFields().stream().map(AbstractEntityField::getField).forEach(field -> {
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
