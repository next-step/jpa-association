package persistence.sql.entity.collection;

import java.util.List;

public class CollectionPersisterImpl implements CollectionPersister {

    private final EntityCollectionContext entityCollectionContext;

    public CollectionPersisterImpl() {
        this.entityCollectionContext = new EntityCollectionContext();
    }

    @Override
    public List<Object> getEntity(Class<?> clazz, Object id) {
        return entityCollectionContext.getCollection(clazz.getSimpleName(), id);
    }

    @Override
    public void addEntity(Class<?> clazz, Object id, List<Object> lazyEntity) {
        entityCollectionContext.addCollection(
                clazz.getSimpleName(),
                id,
                lazyEntity);
    }
}
