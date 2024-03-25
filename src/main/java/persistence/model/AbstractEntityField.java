package persistence.model;

import jakarta.persistence.*;
import persistence.ReflectionUtils;
import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;

public abstract class AbstractEntityField {

    public static AbstractEntityField createEntityField(final Field field) {
        if (field.isAnnotationPresent(Transient.class)) {
            throw new MetaDataModelMappingException(field.getName() + " is Transient field.");
        }

        if (field.isAnnotationPresent(Id.class)) {
            return new EntityId(field.getName(), ColumnBinder.toColumnName(field), field.getClass(), field);
        }

        return new EntityField(field.getName(), ColumnBinder.toColumnName(field), field.getClass(), field);
    }

    private final String fieldName;
    private final String columnName;
    private final Class<?> entityClass;
    private final Field field;

    protected AbstractEntityField(final String fieldName, final String columnName, final Class<?> entityClass, final Field field) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.entityClass = entityClass;
        this.field = field;
    }

    public boolean isJoinField() {
        final boolean oneToOneField = this.field.isAnnotationPresent(OneToOne.class);
        final boolean oneToManyField = this.field.isAnnotationPresent(OneToMany.class);
        final boolean manyToOneField = this.field.isAnnotationPresent(ManyToOne.class);
        final boolean manyToManyField = this.field.isAnnotationPresent(ManyToMany.class);
        return oneToOneField|| oneToManyField || manyToOneField || manyToManyField;
    }

    public Field getField() {
        return this.field;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setValue(final Object entity, final Object value) {
        ReflectionUtils.setFieldValue(this.field, entity, value);
    }
}
