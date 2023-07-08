package persistence.entity;

import java.util.Optional;

public interface EntityManager {
    <T> Optional<T> find(Class<T> clazz, Object id);

    <T> T persist(T entity);

    void remove(Object entity);

    boolean isDirty(Object entity);
}
