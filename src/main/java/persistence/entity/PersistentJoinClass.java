package persistence.entity;

import persistence.ReflectionUtils;
import persistence.model.EntityJoinEntityField;
import persistence.model.EntityMetaData;
import persistence.model.EntityMetaDataMapping;
import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;
import java.util.Objects;

public class PersistentJoinClass {

    private final Class<?> clazz;
    private final EntityMetaData entityMetaData;
    private final EntityJoinEntityField joinEntityField;
    private final PersistentClassFields fields = new PersistentClassFields();

    public PersistentJoinClass(EntityJoinEntityField joinEntityField) {
        this.clazz = joinEntityField.getFieldType();
        this.joinEntityField = joinEntityField;
        this.entityMetaData = EntityMetaDataMapping.getMetaData(this.clazz.getName());
    }

    public void clearValue() {
        this.fields.init();
    }

    public boolean isSameTableName(final String tableName) {
        return this.entityMetaData.isSameTableName(tableName);
    }

    public Field getJoinedEntityField() {
        return this.joinEntityField.getField();
    }

    public void setFieldValue(final String columnLabel, final Object value) {
        fields.setFieldValue(columnLabel, value);
    }

    public Object getEntity() {
        if (this.fields.isEmpty()) {
            return null;
        }

        final Object entity = ReflectionUtils.createInstance(clazz);

        this.entityMetaData.getFields().forEach(field -> {
            final String columnName = ColumnBinder.toColumnName(field);
            final Object columnValue = fields.getFieldValue(columnName);

            if (Objects.isNull(columnValue)) {
                return;
            }

            ReflectionUtils.setFieldValue(field, entity, columnValue);
        });

        return entity;
    }
}
