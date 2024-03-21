package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityJoinEntityField;
import persistence.model.EntityMetaData;
import persistence.model.EntityMetaDataMapping;
import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;
import java.util.*;

public class PersistentClass<T> {

    private final Class<T> clazz;
    private T entity;
    private final EntityMetaData entityMetaData;
    private final List<PersistentJoinClass> persistentJoinClasses = new ArrayList<>();
    private final Map<String, Object> valueMap = new LinkedHashMap<>();

    public PersistentClass(final Class<T> clazz) {
        this.clazz = clazz;
        this.entity = ReflectionUtils.createInstance(this.clazz);
        this.entityMetaData = EntityMetaDataMapping.getMetaData(this.clazz.getName());
    }

    public void initPersistentClass() {
        valueMap.clear();

        if (this.persistentJoinClasses.isEmpty()) {
            this.entityMetaData.getJoinFields().stream()
                    .filter(EntityJoinEntityField::isNotLazy)
                    .forEach(this::addJoinedEntityClass);
            return;
        }

        this.persistentJoinClasses.forEach(PersistentJoinClass::initEntity);
    }

    private void addJoinedEntityClass(final EntityJoinEntityField joinEntityField) {
        this.persistentJoinClasses.add(new PersistentJoinClass(joinEntityField));
    }

    public void setFieldValue(final String tableName, final String columnLabel, final Object value) {
        if (entityMetaData.isSameTableName(tableName)) {
            valueMap.put(columnLabel.toLowerCase(), value);
            return;
        }

        final PersistentJoinClass persistentJoinClass = getByTableName(tableName);
        persistentJoinClass.setEntityField(columnLabel, value);
    }

    private PersistentJoinClass getByTableName(final String tableName) {
        return this.persistentJoinClasses.stream().filter(clazz -> clazz.isSameTableName(tableName))
                .findFirst()
                .get();
    }

    public T getEntity() {
        final Field idField = entityMetaData.getIdField();
        final String idColumnName = ColumnBinder.toColumnName(idField);

        if (ReflectionUtils.isDifferentFieldValue(idField, this.entity, this.valueMap.get(idColumnName))) {
            this.entity = initEntity();
        }
        this.persistentJoinClasses.forEach(clazz -> ReflectionUtils.setFieldValue(clazz.getJoinedEntityField(), this.entity, clazz.getJoinedEntity()));

        return this.entity;
    }

    private T initEntity() {
        final T entity = ReflectionUtils.createInstance(this.clazz);
        entityMetaData.getFields().forEach(field -> {
            final String columnName = ColumnBinder.toColumnName(field).toLowerCase();
            final Object columnValue = valueMap.get(columnName);

            if (Objects.nonNull(columnValue)) {
                ReflectionUtils.setFieldValue(field, entity, columnValue);
            }
        });

        return entity;
    }

}
