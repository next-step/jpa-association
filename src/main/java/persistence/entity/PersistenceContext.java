package persistence.entity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.sql.Entity;
import persistence.sql.Id;

public class PersistenceContext {
    private final Map<Id, Entity> entitiesByKey = new ConcurrentHashMap<>();

    public Object getEntity(Class<?> entityClass, Object id) {
        final Entity entity = entitiesByKey.get(new Id(entityClass, id));
        if (entity == null) {
            return null;
        }
        return entity.getEntity();
    }

    public void addEntity(Object entity) {
        entitiesByKey.put(new Id(entity), new Entity(entity));
    }

    public void removeEntity(Object entity) {
        entitiesByKey.remove(new Id(entity));
    }

    public boolean contains(Object entity) {
        return entitiesByKey.containsKey(new Id(entity));
    }

    private <T> Object getIdValue(Class<T> clazz, Object instance) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                field.setAccessible(true);
                try {
                    return field.get(instance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("No id column found");
    }
}
