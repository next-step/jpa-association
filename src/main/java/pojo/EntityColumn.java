package pojo;

import jakarta.persistence.GeneratedValue;

import java.lang.reflect.Field;
import java.util.Objects;

public class EntityColumn {

    private final Field field;
    private final FieldName fieldName;
    private final FieldValue fieldValue;

    public EntityColumn(Field field, Object entity) {
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("field 가 null 이어서는 안됩니다.");
        }
        this.field = field;
        this.fieldName = new FieldName(field);
        this.fieldValue = new FieldValue(field, entity);
    }

    public Field getField() {
        return field;
    }

    public FieldName getFieldName() {
        return fieldName;
    }

    public FieldValue getFieldValue() {
        return fieldValue;
    }

    public boolean hasGenerationType() {
        return field.isAnnotationPresent(GeneratedValue.class);
    }
}
