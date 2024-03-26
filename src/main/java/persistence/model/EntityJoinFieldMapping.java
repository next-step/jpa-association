package persistence.model;

import jakarta.persistence.FetchType;
import persistence.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class EntityJoinFieldMapping {
    protected Class<? extends Annotation> annotationClass;

    public boolean support(final Field field) {
        return field.isAnnotationPresent(annotationClass);
    }

    public Class<?> getEntityType(final Field field) {
        return ReflectionUtils.mapToGenericClass(field);
    }

    protected abstract FetchType getFetchType(final Field field);

    public boolean isLazy(final Field field) {
        return this.getFetchType(field) == FetchType.LAZY;
    }

    public <T> CollectionPersistentClass createCollectionPersistentClass(final PersistentClass<?> persistentClass) {
        return new CollectionPersistentClass(persistentClass);
    }
}
