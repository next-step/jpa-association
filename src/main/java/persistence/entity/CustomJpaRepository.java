package persistence.entity;

import persistence.core.EntityMetadata;
import persistence.entity.manager.EntityManager;
import persistence.util.ReflectionUtils;

import java.util.Objects;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;
    private final EntityMetadata<T> entityMetadata;

    public CustomJpaRepository(final EntityManager entityManager, final EntityMetadata<T> entityMetadata) {
        this.entityManager = entityManager;
        this.entityMetadata = entityMetadata;
    }

    public T save(final T t) {
        if (isNew(t)) {
            entityManager.persist(t);
            return t;
        }

        return entityManager.merge(t);
    }

    public T findById(final ID id) {
        return entityManager.find(entityMetadata.getType(), id);
    }

    private boolean isNew(final T t) {
        final String idColumnFieldName = entityMetadata.getIdColumnFieldName();
        final Object idValue = ReflectionUtils.getFieldValue(t, idColumnFieldName);

        return Objects.isNull(idValue);
    }
}
