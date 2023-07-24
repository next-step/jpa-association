package persistence.entity;

import java.util.List;
import java.util.Optional;

public interface EntityManager {
    <T> List<T> findAll(Class<T> clazz);

    <T> Optional<T> find(Class<T> clazz, Object id);

    void persist(Object entity);

    void remove(Object entity);

    void merge(Object entity);

    void detach(Object entity);

    boolean isDirty(Object entity);

    boolean isNew(Object entity);
}
