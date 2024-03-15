package persistence.entity;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import persistence.sql.model.Table;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister persister;
    private final EntityLoader loader;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(EntityPersister persister, EntityLoader loader) {
        this.persister = persister;
        this.loader = loader;
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, EntityId id) {
        EntityEntry entry = persistenceContext.getEntityEntry(clazz, id);

        if (entry == null) {
            T findEntity = loader.read(clazz, id);
            persistenceContext.addEntity(id, findEntity);
            return findEntity;
        }

        Status status = entry.status();

        if (status == Status.GONE) {
            throw new EntityNotFoundException();
        }

        if (status == Status.MANAGED) {
            return persistenceContext.getEntity(clazz, id);
        }

        entry.loading();
        T findEntity = loader.read(clazz, id);
        persistenceContext.addEntity(id, findEntity);
        return findEntity;
    }

    @Override
    public <T> T getReference(Class<T> clazz, EntityId id) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(clazz, id);

        if (entityEntry == null) {
            T entityProxy = createEntityProxy(clazz, id);
            persistenceContext.addEntity(id, entityProxy);
            return entityProxy;
        }

        Status status = entityEntry.status();

        if (status == Status.GONE) {
            throw new EntityNotFoundException();
        }

        if (status == Status.MANAGED) {
            return persistenceContext.getEntity(clazz, id);
        }

        entityEntry.loading();
        T entityProxy = createEntityProxy(clazz, id);
        persistenceContext.addEntity(id, entityProxy);
        return entityProxy;
    }

    private <T> T createEntityProxy(Class<T> clazz, EntityId id) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallbacks(new Callback[]{
                new EntityGetIdProxy(id),
                new EntityLazyLoader(this, clazz, id)
        });
        enhancer.setCallbackFilter(new EntityCallbackFilter());
        return (T) enhancer.create();
    }

    @Override
    public void persist(Object entity) {
        if (isExist(entity)) {
            throw new EntityExistsException();
        }
        EntityId id = persister.create(entity);
        persistenceContext.addEntity(id, entity);
    }

    @Override
    public void merge(Object entity) {
        if (!isExist(entity)) {
            EntityId id = persister.create(entity);
            bindEntityId(entity, id);
            persistenceContext.addEntity(id, entity);
            return;
        }

        EntityEntry entry = persistenceContext.getEntityEntry(entity);
        if (entry == null) {
            EntityId id = persister.update(entity);
            bindEntityId(entity, id);
            persistenceContext.addEntity(id, entity);
            return;
        }

        Status status = entry.status();
        if (status == Status.READ_ONLY) {
            return;
        }

        if (isDirty(entity)) {
            entry.saving();

            EntityId id = persister.update(entity);
            persistenceContext.addEntity(id, entity);
        }
    }

    private void bindEntityId(Object entity, EntityId id) {
        EntityBinder entityBinder = new EntityBinder(entity);
        entityBinder.bindEntityId(id);
    }

    private boolean isExist(Object entity) {
        return persistenceContext.isCached(entity) || loader.isExist(entity);
    }

    private boolean isDirty(Object entity) {
        EntityBinder entityBinder = new EntityBinder(entity);
        EntityId id = entityBinder.getEntityId();

        Object snapshot = getDatabaseSnapshot(entity, id);
        EntityBinder snapshotBinder = new EntityBinder(snapshot);

        Table table = createTable(entity);
        return table.getColumns()
                .stream()
                .anyMatch(column -> {
                    Object entityValue = entityBinder.getValue(column);
                    Object snapshotValue = snapshotBinder.getValue(column);
                    return !Objects.equals(entityValue, snapshotValue);
                });
    }

    private Object getDatabaseSnapshot(Object entity, EntityId id) {
        Object snapshot = persistenceContext.getDatabaseSnapshot(id, entity);
        if (snapshot != null) {
            return snapshot;
        }

        Class<?> clazz = entity.getClass();
        return find(clazz, id);
    }

    private Table createTable(Object entity) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        Class<?> clazz = entity.getClass();
        return entityMetaCache.getTable(clazz);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        persister.delete(entity);

        EntityEntry entry = persistenceContext.getEntityEntry(entity);
        entry.gone();
    }
}
