package persistence.repository;

import persistence.entity.manager.EntityManager;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        if (isNewEntity(entity)) {
            entityManager.persist(entity);
            return entity;
        }

        return entityManager.merge(entity);
    }

    private boolean isNewEntity(T entity) {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(entity.getClass());
        return entityMeta.isNew(entity);
    }
}
