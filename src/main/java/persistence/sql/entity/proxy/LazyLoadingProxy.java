package persistence.sql.entity.proxy;

import net.sf.cglib.proxy.LazyLoader;
import persistence.sql.entity.collection.CollectionLoader;
import persistence.sql.entity.collection.CollectionPersister;

import java.util.List;

public class LazyLoadingProxy implements LazyLoader {

    private final CollectionPersister collectionPersister;
    private final CollectionLoader collectionLoader;
    private final Class<?> clazz;
    private final Object id;

    public LazyLoadingProxy(final CollectionPersister collectionPersister,
                            final CollectionLoader collectionLoader,
                            final Class<?> clazz,
                            final Object id) {
        this.collectionPersister = collectionPersister;
        this.collectionLoader = collectionLoader;
        this.clazz = clazz;
        this.id = id;
    }

    @Override
    public Object loadObject() throws Exception {
        System.out.println("Lazy loading 발생");
        List<Object> entity = collectionPersister.getEntity(clazz, id);
        if(entity != null) {
            return entity;
        }

        List<Object> selectEntity = collectionLoader.findById(clazz, id);
        if(selectEntity == null) {
            collectionPersister.addEntity(clazz, id, selectEntity);
        }
        return selectEntity;
    }
}
