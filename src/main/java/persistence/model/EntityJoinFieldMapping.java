package persistence.model;

import jakarta.persistence.FetchType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class EntityJoinFieldMapping {
    protected Class<? extends Annotation> annotationClass;

    public boolean support(final Field field) {
        return field.isAnnotationPresent(annotationClass);
    }

    public EntityJoinEntityField create(final Field field) {
        return new EntityJoinEntityField(getEntityType(field), field, getFetchType(field));
    }

    protected Class<?> getEntityType(final Field field) {
        return field.getClass();
    }

    protected abstract FetchType getFetchType(final Field field);
}
