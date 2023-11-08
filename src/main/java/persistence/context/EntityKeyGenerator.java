package persistence.context;

import persistence.core.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class EntityKeyGenerator {

    private final Map<Class<?>, Map<Object, EntityKey>> cache;

    public EntityKeyGenerator() {
        this.cache = new HashMap<>();
    }

    public EntityKey generate(final Class<?> entityClass, final Object key) {
        return cache.computeIfAbsent(entityClass, this::createKeyCacheForClass)
                .computeIfAbsent(key, object -> EntityKey.of(new EntityMetadata<>(entityClass), key));
    }

    private Map<Object, EntityKey> createKeyCacheForClass(final Class<?> entityClass) {
        return new HashMap<>();
    }

}
