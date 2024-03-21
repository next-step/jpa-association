package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityJoinEntityField;
import persistence.model.EntityMetaDataMapping;

import java.util.ArrayList;
import java.util.List;

public class PersistentClass<T> extends AbstractPersistentClass {

    private final List<PersistentJoinClass> persistentJoinClasses = new ArrayList<>();

    public PersistentClass(final Class<T> clazz) {
        super(clazz, EntityMetaDataMapping.getMetaData(clazz.getName()));
        this.entity = ReflectionUtils.createInstance(this.clazz);
        this.entityMetaData.getJoinFields().stream()
                .filter(EntityJoinEntityField::isNotLazy)
                .forEach(this::addJoinedEntityClass);
    }

    @Override
    public void initPersistentClass() {
        super.initPersistentClass();
        this.persistentJoinClasses.forEach(PersistentJoinClass::initPersistentClass);
    }

    private void addJoinedEntityClass(final EntityJoinEntityField joinEntityField) {
        this.persistentJoinClasses.add(new PersistentJoinClass(joinEntityField));
    }

    public void setFieldValue(final String tableName, final String columnLabel, final Object value) {
        if (entityMetaData.isSameTableName(tableName)) {
            setInternalFieldValue(columnLabel, value);
            return;
        }

        setJoinClassFieldValue(tableName, columnLabel, value);
    }

    private void setJoinClassFieldValue(final String tableName, final String columnLabel, final Object value) {
        this.persistentJoinClasses.stream().filter(clazz -> clazz.isSameTableName(tableName))
                .findFirst()
                .ifPresent(clazz -> clazz.setInternalFieldValue(columnLabel, value));
    }

    @Override
    public T getEntity() {
        super.getEntity();
        this.persistentJoinClasses.forEach(clazz -> ReflectionUtils.setFieldValue(clazz.getJoinedEntityField(), this.entity, clazz.getEntity()));

        return (T) this.entity;
    }

}
