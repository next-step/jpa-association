package persistence.persistencecontext;

import persistence.entity.EntityEntry;
import persistence.entity.EntityMeta;
import persistence.entity.EntityEntryStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> entities = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> snapshots = new HashMap<>();
    private final Map<Object, EntityEntry> entries = new HashMap<>();

    @Override
    public Optional<Object> getEntity(Class<?> clazz, Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        return Optional.ofNullable(entities.get(entityKey));
    }

    @Override
    public void addEntity(Object entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        entries.put(entity, new EntityEntry(EntityEntryStatus.MANAGED));
        entities.put(entityMeta.getEntityKey(entity), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        EntityEntry entityEntry = entries.get(entity);
        entityEntry.updateStatus(EntityEntryStatus.DELETED);
        entities.remove(entityMeta.getEntityKey(entity));
        entityEntry.updateStatus(EntityEntryStatus.GONE);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        EntityKey entityKey = entityMeta.getEntityKey(entity);
        return snapshots.put(entityKey, EntitySnapshot.from(entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        EntityKey entityKey = entityMeta.getEntityKey(entity);
        return snapshots.get(entityKey);
    }

    @Override
    public void addEntityEntry(Object entity, EntityEntryStatus entityEntryStatus) {
        entries.put(entity, new EntityEntry(entityEntryStatus));
    }

    @Override
    public List<Object> getDirtyEntities() {
        return snapshots.keySet().stream()
                .filter(key -> snapshots.get(key).isChanged(entities.get(key)))
                .map(entities::get)
                .collect(Collectors.toList());
    }
}
