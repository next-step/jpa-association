package persistence;

import builder.dml.EntityData;

public interface PersistenceContext {

    EntityData findEntity(EntityKey entityKey);

    void insertEntity(EntityKey entityKey, EntityData EntityData);

    void deleteEntity(EntityKey entityKey);

    void insertDatabaseSnapshot(EntityKey entityKey, EntityData EntityData);

    EntityData getDatabaseSnapshot(EntityKey entityKey);

    void deleteDatabaseSnapshot(EntityKey entityKey);

    void insertEntityEntryMap(EntityKey entityKey, EntityStatus entityStatus);

    EntityEntry getEntityEntryMap(EntityKey entityKey);

}
