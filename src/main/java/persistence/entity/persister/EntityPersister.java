package persistence.entity.persister;

import java.util.List;
import persistence.entity.EntityKey;

public interface EntityPersister {
    <T> T insert(T entity);

    <T> T find(Class<T> tClass, Object id);

    <T> List<T> findAll(Class<T> tClass);

    boolean update(Object entity);

    void deleteByKey(EntityKey entityKey);
}
