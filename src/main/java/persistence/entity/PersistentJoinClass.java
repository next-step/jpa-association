package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityJoinEntityField;

import java.lang.reflect.Field;

public class PersistentJoinClass {

    private final EntityJoinEntityField joinEntityField;
    private Object joinedEntity;

    public PersistentJoinClass(EntityJoinEntityField joinEntityField) {
        this.joinEntityField = joinEntityField;
        initEntity();
    }

    protected void initEntity() {
        this.joinedEntity = ReflectionUtils.createInstance(joinEntityField.getFieldType());
    }

    public boolean isSameTableName(final String tableName) {
        return this.joinEntityField.getMetaData().isSameTableName(tableName);
    }

    public Field getJoinedEntityField() {
        return this.joinEntityField.getField();
    }

    public Object getJoinedEntity() {
        return this.joinedEntity;
    }

    public void setEntityField(final String columnLabel, final Object value) {
        this.joinEntityField.getMetaData().getFields()
                .stream().filter(field -> field.getName().equalsIgnoreCase(columnLabel)).findFirst()
                        .ifPresent(field -> ReflectionUtils.setFieldValue(field, joinedEntity, value));
    }
}
