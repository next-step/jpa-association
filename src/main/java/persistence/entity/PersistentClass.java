package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityMetaData;
import persistence.model.EntityMetaDataMapping;
import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;
import java.util.Objects;

public class PersistentClass<T> {

    private final Class<T> clazz;
    private T entity;
    private final EntityMetaData entityMetaData;
    private final PersistentClassFields fields = new PersistentClassFields();

    public PersistentClass(final Class<T> clazz) {
        this.clazz = clazz;
        this.entity = ReflectionUtils.createInstance(this.clazz);
        this.entityMetaData = EntityMetaDataMapping.getMetaData(clazz.getName());
    }

    public void initPersistentClass() {
        this.fields.init();
    }

    public void setFieldValue(final String tableName, final String columnLabel, final Object value) {
        fields.setFieldValue(columnLabel, value);
    }

    public T getEntity() {
        final Field idField = this.entityMetaData.getIdField();
        final String idColumnName = ColumnBinder.toColumnName(idField);

        if (ReflectionUtils.isDifferentFieldValue(idField, this.entity, this.fields.getFieldValue(idColumnName))) {
            this.entity = initEntity(this.clazz);
        }

        return this.entity;
    }

    private T initEntity(final Class<T> clazz) {
        final T entity = ReflectionUtils.createInstance(clazz);
        this.entityMetaData.getFields().forEach(field -> {
            final String columnName = ColumnBinder.toColumnName(field);
            final Object columnValue = this.fields.getFieldValue(columnName);

            if (Objects.isNull(columnValue)) {
                return;
            }

            ReflectionUtils.setFieldValue(field, entity, columnValue);
        });

        return entity;
    }

}
