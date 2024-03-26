package persistence.entity.collection;

import java.util.Set;

public class PersistentSet<T> extends PersistentCollection<T> implements Set<T> {
    public PersistentSet(CollectionLoader collectionLoader, Object joinColumnValue) {
        super(collectionLoader, joinColumnValue);
    }

    @Override
    public boolean add(T t) {
        load();
        if(collection.contains(t)){
            return false;
        }
        return collection.add(t);
    }
}
