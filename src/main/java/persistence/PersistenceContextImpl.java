package persistence;

import builder.dml.EntityData;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, EntityData> entityMap = new HashMap<>();
    private final Map<EntityKey, EntityData> snapShotMap = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntryMap = new HashMap<>();

    @Override
    public EntityData findEntity(EntityKey entityKey) {
        return entityMap.get(entityKey);
    }

    @Override
    public void insertEntity(EntityKey entityKey, EntityData EntityData) {
        this.entityMap.put(entityKey, EntityData);
    }

    @Override
    public void deleteEntity(EntityKey entityKey) {
        this.entityMap.remove(entityKey);
    }

    @Override
    public void insertDatabaseSnapshot(EntityKey entityKey, EntityData EntityData) {
        this.snapShotMap.put(entityKey, EntityData);
    }

    @Override
    public EntityData getDatabaseSnapshot(EntityKey entityKey) {
        return this.snapShotMap.get(entityKey);
    }

    @Override
    public void deleteDatabaseSnapshot(EntityKey entityKey) {
        this.snapShotMap.remove(entityKey);
    }

    @Override
    public void insertEntityEntryMap(EntityKey entityKey, EntityStatus entityStatus) {
        EntityEntry entityEntry = new EntityEntry(entityStatus);
        this.entityEntryMap.put(entityKey, entityEntry);
    }

    @Override
    public EntityEntry getEntityEntryMap(EntityKey entityKey) {
        return this.entityEntryMap.get(entityKey);
    }

}
