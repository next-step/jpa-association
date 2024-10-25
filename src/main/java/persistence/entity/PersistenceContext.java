package persistence.entity;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    EntityEntry getEntityEntry(EntityKey entityKey);

    EntitySnapshot getDatabaseSnapshot(EntityKey entityKey);

    void addEntity(EntityKey entityKey, Object entity);

    void addDatabaseSnapshot(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);

    void addEntry(EntityKey entityKey, EntityEntry entityEntry);
}
