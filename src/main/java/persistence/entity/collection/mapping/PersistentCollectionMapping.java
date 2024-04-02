package persistence.entity.collection.mapping;

import persistence.entity.collection.AbstractPersistentCollection;

public interface PersistentCollectionMapping {
    boolean support(final Class<?> collectionType);

    AbstractPersistentCollection<?> createPersistentCollection();
}
