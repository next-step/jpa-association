package persistence.entity.context.cache;

import persistence.model.PersistentClassMapping;
import persistence.model.PersistentClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntitySnapshot {

    private final PersistentClass<?> persistentClass;
    private final Map<String, Object> values = new HashMap<>();

    public EntitySnapshot(final Object entity) {
        this.persistentClass = PersistentClassMapping.getPersistentClass(entity.getClass().getName());
        values.putAll(persistentClass.extractValues(entity));
    }

    public boolean checkDirty(final Object entity) {
        if (entity.getClass() != persistentClass.getEntityClass()) {
            return false;
        }

        final Map<String, Object> thatValues = persistentClass.extractValues(entity);

        return thatValues.keySet().stream()
                .anyMatch(fieldName -> !Objects.deepEquals(thatValues.get(fieldName), values.get(fieldName)));
    }
}
