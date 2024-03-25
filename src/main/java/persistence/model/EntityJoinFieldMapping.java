package persistence.model;

import jakarta.persistence.FetchType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class EntityJoinFieldMapping {
    protected Class<? extends Annotation> annotationClass;

    public boolean support(final Field field) {
        return field.isAnnotationPresent(annotationClass);
    }

    public Class<?> getEntityType(final Field field) {
        return field.getClass();
    }

    protected abstract FetchType getFetchType(final Field field);

    private boolean isLazy(final Field field) {
        return this.getFetchType(field) == FetchType.LAZY;
    }

    public <T> CollectionPersistentClass createCollectionPersistentClass(final PersistentClass<T> owner, final PersistentClass<?> persistentClass, final Field field) {
        return new CollectionPersistentClass(owner, persistentClass, isLazy(field));
    }
}
