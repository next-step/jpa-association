package persistence.entity;

import java.util.Objects;
import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.entity.persistencecontext.EntitySnapshot;
import persistence.entity.persistencecontext.SimplePersistenceContext;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.meta.Table;

public class SimpleEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;
    private final SimplePersistenceContext persistenceContext;

    private final EntityLoader entityLoader;

    private SimpleEntityManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        entityPersister = SimpleEntityPersister.from(jdbcTemplate);
        entityLoader = SimpleEntityLoader.from(this);
        persistenceContext = new SimplePersistenceContext();
    }

    public static SimpleEntityManager from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityManager(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = (T) persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            entity = entityLoader.find(clazz, id);
            cacheEntityWithAssociations(entity, EntityEntry.loading());
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        entityPersister.insert(entity);
        cacheEntityWithAssociations(entity, EntityEntry.saving());
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.deleted();
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
        entityEntry.gone();
    }

    @Override
    public <T> T merge(T entity) {
        EntitySnapshot before = persistenceContext.getCachedDatabaseSnapshot(entity);
        EntitySnapshot after = EntitySnapshot.from(entity);

        if (!Objects.equals(before, after)) {
            entityPersister.update(entity);
            cacheEntity(entity, EntityEntry.saving());
        }
        return entity;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public <T> void cacheEntityWithAssociations(T entity, EntityEntry entityEntry) {
        cacheEntity(entity, entityEntry);
        cacheAssociations(entity);
        entityEntry.managed();
    }

    private <T> EntityEntry cacheEntity(T t, EntityEntry entityEntry) {
        persistenceContext.addEntity(t);
        persistenceContext.getDatabaseSnapshot(t);
        persistenceContext.setEntityEntry(t, entityEntry);
        return entityEntry;
    }

    private <T> void prepareCacheEntity(T t) {
        if (t instanceof Iterable) {
            ((Iterable<?>)t).forEach(entity -> cacheEntityWithAssociations(entity, EntityEntry.loading()));
            return;
        }
        cacheEntityWithAssociations(t, EntityEntry.loading());
    }

    private <T> void cacheAssociations(T t) {
        Table table = Table.getInstance(t.getClass());
        table.getEagerRelationTables().forEach(relationTable -> {
            Object relationEntity = table.getRelationValue(t, relationTable);
            prepareCacheEntity(relationEntity);
        });
    }
}
