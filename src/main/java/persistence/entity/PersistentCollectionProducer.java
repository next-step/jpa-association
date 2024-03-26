package persistence.entity;

import persistence.entity.collection.CollectionLoader;
import persistence.entity.collection.PersistentCollection;
import persistence.entity.collection.PersistentList;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PersistentCollectionTypeResolver {
    private final Map<Class<?>, BiFunction<CollectionLoader, Object, PersistentCollection<?>>> map;

    public PersistentCollectionTypeResolver() {
        map = new HashMap<>();
        register(List.class, PersistentList::new);
    }


    public void register(Class<?> collectionType, BiFunction<CollectionLoader, Object, PersistentCollection<?>> supplier) {
        map.put(collectionType, supplier);
    }
}
