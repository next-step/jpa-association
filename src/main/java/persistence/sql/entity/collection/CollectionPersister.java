package persistence.sql.entity.collection;

import java.util.List;

public interface CollectionPersister {

    <T> T getEntity(Class<T> clazz, List<Object> lazyEntity);

    void addEntity(Object entity, List<Object> lazyEntity);
}
