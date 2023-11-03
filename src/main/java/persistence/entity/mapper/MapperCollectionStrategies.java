package persistence.entity.mapper;

import persistence.exception.PersistenceException;

import java.util.*;

public class MapperCollectionStrategies {
    private final Map<Class<?>, MapperCollectionStrategy> collectionStrategies;

    public MapperCollectionStrategies() {
        this.collectionStrategies = new HashMap<>();
        collectionStrategies.put(List.class, type -> new ArrayList<>());
        collectionStrategies.put(Set.class, type -> new LinkedHashSet<>());
    }

    public Collection<Object> createCollectionBy(final Class<?> type) {
        final MapperCollectionStrategy mapperCollectionStrategy = collectionStrategies.get(type);
        if (Objects.isNull(mapperCollectionStrategy)) {
            throw new PersistenceException(type.getName() + "은 지원하지 않는 컬렉션 타입입니다.");
        }
        return mapperCollectionStrategy.createCollectionBy(type);
    }
}
