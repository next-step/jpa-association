package persistence.sql.entity.collection;

import java.util.List;

public class CollectionPersisterImpl implements CollectionPersister {

    @Override
    public <T> T getEntity(Class<T> clazz, List<Object> lazyEntity) {
        return null;
    }

    @Override
    public void addEntity(Object entity, List<Object> lazyEntity) {

    }
}
