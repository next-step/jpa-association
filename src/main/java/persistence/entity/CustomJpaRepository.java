package persistence.entity;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        if (entityManager.isNew(entity)) {
            return entityManager.persist(entity);
        }
        if (entityManager.isDirty(entity)) {
            return entityManager.merge(entity);
        }
        return entity;
    }
}
