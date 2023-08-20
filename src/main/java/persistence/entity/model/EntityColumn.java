package persistence.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;

public class EntityColumn {
    private final Field field;

    public EntityColumn(Field field) {
        validate(field);
        field.setAccessible(true);
        this.field = field;
    }

    private void validate(Field field) {
        if (field.isAnnotationPresent(Transient.class)) {
            throw new IllegalArgumentException("@Transient 은 컬럼을 생성할 수 없습니다");
        }
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        Column column = field.getAnnotation(Column.class);
        if (column == null || column.name().isBlank()) {
            return field.getName();
        }
        return column.name();
    }

    public Object getValue(Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(Object entity, Object value) {
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getValueQuery(Object entity) {
        Object value = getValue(entity);
        if (value instanceof String) {
            return String.format("'%s'", value);
        }
        return value.toString();
    }
}
