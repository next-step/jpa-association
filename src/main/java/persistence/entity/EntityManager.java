package persistence.entity;

import java.util.List;
import java.util.Optional;

public interface EntityManager {
    <T> List<T> findAll(Class<T> clazz);

    <T> Optional<T> find(Class<T> clazz, Object id);

    <T> T persist(T entity);

    <T> T merge(T entity);

    void remove(Object entity);

    void detach(Object entity);

    boolean isDirty(Object entity);

    boolean isNew(Object entity);
}
