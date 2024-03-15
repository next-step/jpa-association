package persistence.entity;

import java.util.List;
import java.util.Objects;
import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.entity.persistencecontext.EntitySnapshot;
import persistence.entity.persistencecontext.SimplePersistenceContext;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.entity.proxy.LazyLoadingProxyFactory;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final SimplePersistenceContext persistenceContext;

    private final EntityLoader entityLoader;

    private SimpleEntityManager(JdbcTemplate jdbcTemplate) {
        entityPersister = SimpleEntityPersister.from(jdbcTemplate);
        entityLoader = SimpleEntityLoader.from(jdbcTemplate);
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
            setLazyRelation(Table.getInstance(clazz).getLazyRelationColumns(), entity);
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
            cacheEntity(entity);
        }
        return entity;
    }

    private void cacheEntityWithAssociations(Object entity, EntityEntry entityEntry) {
        prepareEntityForCaching(entity, entityEntry);
        cacheAssociations(entity);
        entityEntry.managed();
    }

    private EntityEntry prepareEntityForCaching(Object entity, EntityEntry entityEntry) {
        cacheEntity(entity);
        persistenceContext.setEntityEntry(entity, entityEntry);
        return entityEntry;
    }

    private void cacheEntity(Object entity) {
        persistenceContext.addEntity(entity);
        persistenceContext.getDatabaseSnapshot(entity);
    }

    private void cacheAssociations(Object entity) {
        Table table = Table.getInstance(entity.getClass());
        table.getEagerRelationTables().forEach(relationTable -> {
            Object relationEntity = table.getRelationValue(entity, relationTable);
            processRelationEntity(relationEntity);
        });
    }

    private void setLazyRelation(List<Column> lazyRelationColumns, Object entity) {
        for (Column lazyRelationColumn : lazyRelationColumns) {
            lazyRelationColumn.setFieldValue(entity, LazyLoadingProxyFactory.create(Table.getInstance(entity.getClass()),
                lazyRelationColumn.getRelationTable(), entity, entityLoader, this::processRelationEntity));
        }
    }

    private void processRelationEntity(Object relationEntity) {
        if (relationEntity instanceof Iterable) {
            ((Iterable<?>)relationEntity).forEach(entity -> cacheEntityWithAssociations(entity, EntityEntry.loading()));
            return;
        }
        cacheEntityWithAssociations(relationEntity, EntityEntry.loading());
    }
}
