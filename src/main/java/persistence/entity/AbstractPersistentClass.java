package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityMetaData;
import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractPersistentClass {

    protected final Class<?> clazz;
    protected Object entity;
    protected final EntityMetaData entityMetaData;
    protected final Map<String, Object> valueMap = new LinkedHashMap<>();

    protected AbstractPersistentClass(Class<?> clazz, EntityMetaData entityMetaData) {
        this.clazz = clazz;
        this.entityMetaData = entityMetaData;
    }

    protected void initPersistentClass() {
        valueMap.clear();
    }

    protected Object initEntity(final Class<?> clazz) {
        final Object entity = ReflectionUtils.createInstance(clazz);
        this.entityMetaData.getFields().forEach(field -> {
            final String columnName = ColumnBinder.toColumnName(field).toLowerCase();
            final Object columnValue = valueMap.get(columnName);

            if (Objects.nonNull(columnValue)) {
                ReflectionUtils.setFieldValue(field, entity, columnValue);
            }
        });

        return entity;
    }

    protected void setInternalFieldValue(final String columnLabel, final Object value) {
        valueMap.put(columnLabel.toLowerCase(), value);
    }

    public Object getEntity() {
        final boolean nullEntity = this.valueMap.entrySet().stream().allMatch((entry) -> Objects.isNull(entry.getValue()));

        if (nullEntity) {
            return null;
        }

        final Field idField = this.entityMetaData.getIdField();
        final String idColumnName = ColumnBinder.toColumnName(idField).toLowerCase();

        if (ReflectionUtils.isDifferentFieldValue(idField, this.entity, this.valueMap.get(idColumnName))) {
            this.entity = initEntity(this.clazz);
        }

        return this.entity;
    }
}
