package persistence.model;

import java.util.HashMap;
import java.util.Map;

public class CollectionPersistentClassBinder {

    private final Map<String, CollectionPersistentClass> collectionPersistentClassMap = new HashMap<>();

    public CollectionPersistentClass getCollectionPersistentClassOrDefault(final String className, final CollectionPersistentClass collectionPersistentClass) {
        return this.collectionPersistentClassMap.computeIfAbsent(className, key -> collectionPersistentClass);
    }

    public CollectionPersistentClass getCollectionPersistentClass(final String className) {
        return this.collectionPersistentClassMap.get(className);
    }
}
