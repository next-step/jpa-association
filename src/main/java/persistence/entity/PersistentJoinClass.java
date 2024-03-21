package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityJoinEntityField;

import java.lang.reflect.Field;

public class PersistentJoinClass extends AbstractPersistentClass {

    private final EntityJoinEntityField joinEntityField;

    public PersistentJoinClass(EntityJoinEntityField joinEntityField) {
        super(joinEntityField.getFieldType(), joinEntityField.getMetaData());
        this.joinEntityField = joinEntityField;
        this.entity = ReflectionUtils.createInstance(this.clazz);
    }

    public boolean isSameTableName(final String tableName) {
        return this.entityMetaData.isSameTableName(tableName);
    }

    public Field getJoinedEntityField() {
        return this.joinEntityField.getField();
    }
}
