package persistence.model;

import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class EntityJoinEntityField {

    private final Class<?> entityClass;
    private final Field field;

    public EntityJoinEntityField(final Field field) {
        if (isOne(field)) {
            this.entityClass = (Class<?>) (((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
        } else {
            this.entityClass = field.getClass();
        }
        this.field = field;
    }

    private boolean isOne(final Field field) {
        return field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(OneToMany.class);
    }

    public Class<?> getFieldType() {
        return this.entityClass;
    }
}
