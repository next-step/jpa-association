package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityFields;
import persistence.model.EntityJoinEntityField;
import persistence.model.EntityMetaData;
import persistence.model.EntityMetaDataMapping;
import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersistentClass<T> {

    private final Class<T> clazz;
    private T entity;
    private final EntityMetaData entityMetaData;
    private final List<PersistentJoinClass> persistentJoinClasses = new ArrayList<>();
    private final PersistentClassFields fields = new PersistentClassFields();

    public PersistentClass(final Class<T> clazz) {
        this.clazz = clazz;
        this.entity = ReflectionUtils.createInstance(this.clazz);
        this.entityMetaData = EntityMetaDataMapping.getMetaData(clazz.getName());
        this.entityMetaData.getJoinFields().stream()
                .filter(EntityJoinEntityField::isNotLazy)
                .forEach(this::addJoinedEntityClass);
    }

    public void initPersistentClass() {
        this.fields.init();
        this.persistentJoinClasses.forEach(PersistentJoinClass::clearValue);
    }

    private void addJoinedEntityClass(final EntityJoinEntityField joinEntityField) {
        this.persistentJoinClasses.add(new PersistentJoinClass(joinEntityField));
    }

    public void setFieldValue(final String tableName, final String columnLabel, final Object value) {
        if (entityMetaData.isSameTableName(tableName)) {
            fields.setFieldValue(columnLabel, value);
            return;
        }

        setJoinClassFieldValue(tableName, columnLabel, value);
    }

    private void setJoinClassFieldValue(final String tableName, final String columnLabel, final Object value) {
        this.persistentJoinClasses.stream().filter(clazz -> clazz.isSameTableName(tableName))
                .findFirst()
                .ifPresent(clazz -> clazz.setFieldValue(columnLabel, value));
    }

    public T getEntity() {
        final Field idField = this.entityMetaData.getIdField();
        final String idColumnName = ColumnBinder.toColumnName(idField);

        if (ReflectionUtils.isDifferentFieldValue(idField, this.entity, this.fields.getFieldValue(idColumnName))) {
            this.entity = initEntity(this.clazz);
        }

        this.persistentJoinClasses.forEach(clazz -> ReflectionUtils.setFieldValue(clazz.getJoinedEntityField(), this.entity, clazz.getEntity()));

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
