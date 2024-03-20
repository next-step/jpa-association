package persistence.sql.entity.collection;

import java.util.List;

public interface CollectionPersister {

    List<Object> getEntity(Class<?> clazz, Object id);

    void addEntity(Class<?> clazz, Object id, List<Object> lazyEntity);
}
