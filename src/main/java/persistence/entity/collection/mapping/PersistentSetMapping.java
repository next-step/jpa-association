package persistence.entity.collection.mapping;

import persistence.entity.collection.AbstractPersistentCollection;
import persistence.entity.collection.PersistentSet;

import java.util.Set;

public class PersistentSetMapping implements PersistentCollectionMapping {
    @Override
    public boolean support(final Class<?> collectionType) {
        return collectionType.equals(Set.class);
    }

    @Override
    public AbstractPersistentCollection<?> createPersistentCollection() {
        return new PersistentSet<>();
    }
}
