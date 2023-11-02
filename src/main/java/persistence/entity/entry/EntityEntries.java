package persistence.entity.entry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityEntries {
    private final Map<Object, EntityEntry> entityEntries = new HashMap<>();

    public void changeOrSetStatus(Status toStatus, Object instance) {
        entityEntries.putIfAbsent(instance, new SimpleEntityEntry(toStatus));
    }

    public EntityEntry getEntityEntry(Object instance) {
        return Optional.of(entityEntries.get(instance)).orElse(null);

    }
}
