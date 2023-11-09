package persistence.entity.manager;

import persistence.context.EntityKey;
import persistence.context.EntityKeyGenerator;
import persistence.context.PersistenceContext;
import persistence.context.SimplePersistenceContext;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.entity.entry.Status;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;
import persistence.entity.proxy.EntityProxyFactory;
import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    private final EntityMetadataProvider entityMetadataProvider;
    private final EntityPersisters entityPersisters;
    private final EntityLoaders entityLoaders;
    private final EntityProxyFactory entityProxyFactory;
    private final PersistenceContext persistenceContext;
    private final EntityKeyGenerator entityKeyGenerator;

    public SimpleEntityManager(final EntityMetadataProvider entityMetadataProvider, final EntityPersisters entityPersisters, final EntityLoaders entityLoaders, final EntityProxyFactory entityProxyFactory) {
        this.entityMetadataProvider = entityMetadataProvider;
        this.entityPersisters = entityPersisters;
        this.entityLoaders = entityLoaders;
        this.entityProxyFactory = entityProxyFactory;
        this.entityKeyGenerator = new EntityKeyGenerator();
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        final EntityLoader<T> entityLoader = entityLoaders.getEntityLoader(clazz);
        final EntityKey entityKey = entityKeyGenerator.generate(clazz, id);
        final Object entity = persistenceContext.getEntity(entityKey)
                .orElseGet(() -> initEntity(entityMetadataProvider.getEntityMetadata(clazz), entityKey, entityLoader));
        return clazz.cast(entity);
    }

    @Override
    public void persist(final Object entity) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(entity.getClass());

        final Object idValue = extractId(entity, entityPersister);
        final boolean hasIdValue = !Objects.isNull(idValue);
        if (hasIdValue) {
            checkEntityAlreadyExists(entity, idValue);
            processUpdate(entity, entityPersister);
            return;
        }

        processInsert(entity, entityPersister);
    }

    @Override
    public <T> T merge(final T entity) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(entity.getClass());

        final Object idValue = extractId(entity, entityPersister);
        final boolean isIdValueNull = Objects.isNull(idValue);
        if (isIdValueNull) {
            throw new PersistenceException("Id value 없이 merge 할 수 없습니다.");
        }

        processUpdate(entity, entityPersister);
        return entity;
    }

    @Override
    public void remove(final Object entity) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(entity.getClass());
        final Object idValue = extractId(entity, entityPersister);

        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        persistenceContext.removeEntity(entityKey);

        persistenceContext.updateEntityEntryStatus(entity, Status.DELETED);

        entityPersister.delete(entity);
    }

    private Object extractId(final Object entity, final EntityPersister entityPersister) {
        return ReflectionUtils.getFieldValue(entity, entityPersister.getIdColumnFieldName());
    }

    private <T> Object initEntity(final EntityMetadata<T> entityMetadata, final EntityKey entityKey, final EntityLoader<T> entityLoader) {
        final Object key = entityKey.getKey();
        final Object entityFromDatabase = entityLoader.loadById(key)
                .orElseThrow(() -> new PersistenceException("존재하지 않는 entity 입니다."));

        entityMetadata.getLazyOneToManyColumns()
                .forEach(oneToManyColumn -> entityProxyFactory.initProxy(key, entityFromDatabase, oneToManyColumn));

        persistenceContext.addEntityEntry(entityFromDatabase, Status.LOADING);
        persistenceContext.addEntity(entityKey, entityFromDatabase);
        persistenceContext.getDatabaseSnapshot(entityKey, entityFromDatabase);

        return entityFromDatabase;
    }

    private void checkEntityAlreadyExists(final Object entity, final Object idValue) {
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        if (persistenceContext.hasEntity(entityKey)) {
            throw new PersistenceException("이미 persistence context에 존재하는 entity 는 persist 할 수 없습니다.");
        }
    }

    private void processInsert(final Object entity, final EntityPersister entityPersister) {
        persistenceContext.addEntityEntry(entity, Status.SAVING);

        entityPersister.insert(entity);

        final Object idValue = extractId(entity, entityPersister);
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        persistenceContext.addEntity(entityKey, entity);
    }

    private void processUpdate(final Object entity, final EntityPersister entityPersister) {
        final Object idValue = extractId(entity, entityPersister);
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);

        Object foundEntity = null;
        if (!persistenceContext.hasEntity(entityKey)) {
            foundEntity = find(entity.getClass(), idValue);
        }

        if (isDirty(entity)) {
            updateEntityEntry(foundEntity, entity);
            entityPersister.update(entity);
            persistenceContext.addEntity(entityKey, entity);
        }
    }

    private void updateEntityEntry(final Object foundEntity, final Object entity) {
        if (Objects.nonNull(foundEntity)) {
            persistenceContext.updateEntityEntryStatus(foundEntity, Status.GONE);
            persistenceContext.addEntityEntry(entity, Status.SAVING);
        }
    }

    private boolean isDirty(final Object entity) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(entity.getClass());
        final Object idValue = extractId(entity, entityPersister);
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        final Object databaseSnapshot = persistenceContext.getDatabaseSnapshot(entityKey, entity);

        return entityPersister.getColumnFieldNames()
                .stream()
                .anyMatch(columnName -> hasDifferentValue(entity, databaseSnapshot, columnName));
    }

    private boolean hasDifferentValue(final Object entity, final Object cachedDatabaseSnapshot, final String columnName) {
        final Object targetValue = ReflectionUtils.getFieldValue(entity, columnName);
        final Object snapshotValue = ReflectionUtils.getFieldValue(cachedDatabaseSnapshot, columnName);
        return !Objects.equals(targetValue, snapshotValue);
    }

}
