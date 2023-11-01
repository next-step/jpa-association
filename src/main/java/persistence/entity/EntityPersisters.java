package persistence.entity;

import java.util.Map;

public class EntityPersisters {
    private final Map<Class<?>, EntityPersister> cache;

    public EntityPersisters(final Map<Class<?>, EntityPersister> persisters) {
        this.cache = persisters;
    }

    public EntityPersister getEntityPersister(final Class<?> clazz) {
        return cache.get(clazz);
    }

}
