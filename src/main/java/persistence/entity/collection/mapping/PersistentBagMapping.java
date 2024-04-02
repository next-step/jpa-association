package persistence.entity.collection.mapping;

import persistence.entity.collection.AbstractPersistentCollection;
import persistence.entity.collection.PersistentBag;

import java.util.List;

public class PersistentBagMapping implements PersistentCollectionMapping {
    @Override
    public boolean support(final Class<?> collectionType) {
        return collectionType.equals(List.class);
    }

    @Override
    public AbstractPersistentCollection<?> createPersistentCollection() {
        return new PersistentBag<>();
    }
}
