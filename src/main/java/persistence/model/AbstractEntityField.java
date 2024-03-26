package persistence.model;

import jakarta.persistence.*;
import persistence.ReflectionUtils;
import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;

public abstract class AbstractEntityField {

    public static AbstractEntityField createEntityField(final Field field) {
        final String fieldName = field.getName();

        if (field.isAnnotationPresent(Transient.class)) {
            throw new MetaDataModelMappingException(fieldName + " is Transient field.");
        }

        final String columnName = ColumnBinder.toColumnName(field);
        final Class<? extends Field> fieldClass = field.getClass();

        if (field.isAnnotationPresent(Id.class)) {
            return new EntityId(fieldName, columnName, fieldClass, field);
        } else if (isJoinField(field)) {
            return new EntityJoinField(fieldName, columnName, fieldClass, field);
        }

        return new EntityField(fieldName, columnName, fieldClass, field);
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

    private static boolean isJoinField(final Field field) {
        final boolean oneToOneField = field.isAnnotationPresent(OneToOne.class);
        final boolean oneToManyField = field.isAnnotationPresent(OneToMany.class);
        final boolean manyToOneField = field.isAnnotationPresent(ManyToOne.class);
        final boolean manyToManyField = field.isAnnotationPresent(ManyToMany.class);
        return oneToOneField|| oneToManyField || manyToOneField || manyToManyField;
    }

    public boolean isJoinField() {
        return isJoinField(this.field);
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
