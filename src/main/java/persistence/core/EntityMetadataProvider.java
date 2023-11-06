package persistence.core;

import persistence.exception.PersistenceException;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EntityMetadataProvider {

    private final Map<Class<?>, EntityMetadata<?>> cache;

    private EntityMetadataProvider() {
        this.cache = new ConcurrentHashMap<>();
    }

    public static EntityMetadataProvider getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> EntityMetadata<T> getEntityMetadata(final Class<T> clazz) {
        final EntityMetadata<?> entityMetadata = cache.get(clazz);
        if (Objects.isNull(entityMetadata)) {
            throw new PersistenceException("EntityMetadata 가 초기화 되지 않았습니다.");
        }
        return (EntityMetadata<T>) entityMetadata;
    }


    public void init(final EntityScanner entityScanner) {
        entityScanner.getEntityClasses()
                .forEach(entity -> cache.put(entity, new EntityMetadata<>(entity)));
    }

    public Set<EntityMetadata<?>> getAllAssociatedEntitiesMetadata(final EntityMetadata<?> entityMetadata) {
        return cache.values().stream()
                .filter(metadata -> metadata.hasAssociatedOf(entityMetadata))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static class InstanceHolder {
        private static final EntityMetadataProvider INSTANCE = new EntityMetadataProvider();
    }

}
