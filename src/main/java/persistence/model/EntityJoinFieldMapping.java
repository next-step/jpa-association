package persistence.model;

import jakarta.persistence.FetchType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class EntityJoinFieldMapping {
    protected Class<? extends Annotation> annotationClass;

    public boolean support(final Field field) {
        return field.isAnnotationPresent(annotationClass);
    }

    public EntityJoinEntityField create(final EntityMetaData metaData, final Field field) {
        return new EntityJoinEntityField(metaData, field, getFetchType(field));
    }

    public Class<?> getEntityType(final Field field) {
        return field.getClass();
    }

    protected abstract FetchType getFetchType(final Field field);
}
