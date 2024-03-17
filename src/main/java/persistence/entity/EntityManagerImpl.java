package persistence.entity;

import database.dialect.Dialect;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.part.ValueMap;
import jdbc.JdbcTemplate;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.data.EntitySnapshot;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.Objects;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final CollectionLoader collectionLoader;

    private EntityManagerImpl(PersistenceContext persistenceContext, EntityLoader entityLoader,
                              EntityPersister entityPersister, CollectionLoader collectionLoader) {
        this.persistenceContext = persistenceContext;
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
        this.collectionLoader = collectionLoader;
    }

    public static EntityManagerImpl from(JdbcTemplate jdbcTemplate, Dialect dialect) {
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, dialect);
        return new EntityManagerImpl(
                new PersistenceContextImpl(),
                entityLoader,
                new EntityPersister(jdbcTemplate),
                new CollectionLoader(entityLoader, jdbcTemplate, dialect)
        );
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object cached = persistenceContext.getEntity(clazz, id);
        if (Objects.isNull(cached)) {
            loadEntity(clazz, id);
        }
        return (T) persistenceContext.getEntity(clazz, id);
    }

    private <T> void loadEntity(Class<T> clazz, Long id) {
        if (hasAssociation(clazz)) {
            collectionLoader.load(clazz, id).ifPresent(persistenceContext::addEntity);
        } else {
            entityLoader.load(clazz, id).ifPresent(persistenceContext::addEntity);
        }
    }

    private static <T> boolean hasAssociation(Class<T> clazz) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        return entityMetadata.hasAssociation();
    }

    @Override
    public <T> T persist(Object entity) {
        Class<?> clazz = entity.getClass();

        Long id = getRowId(entity);
        if (isInsertOperation(clazz, id)) {
            return (T) insertEntity(entity, clazz);
        }
        return (T) updateEntity(entity, clazz, id);
    }

    private <T> T insertEntity(Object entity, Class<T> clazz) {
        Long newId = entityPersister.insert(clazz, entity);
        T load = entityLoader.load(clazz, newId).get();
        // TODO: lazy/eager 분리할 때 여길 깔끔하게 할 수 있을까?
        if (!hasAssociation(clazz)) {
            persistenceContext.addEntity(load);
        }
        return load;
    }

    private Object updateEntity(Object entity, Class<?> clazz, Long id) {
        Object oldEntity = find(clazz, id);
        ValueMap diff = EntitySnapshot.of(oldEntity).diff(EntitySnapshot.of(entity));
        if (!diff.isEmpty()) {
            entityPersister.update(clazz, id, diff);
            persistenceContext.addEntity(entity);
        }
        return entity;
    }

    /**
     * id 가 없으면 insert, id 가 있으면 insert 일수도 아닐 수도 있다.
     * 비즈니스 로직에서 잘 넣어줬을 거라고 생각하고, 현재 퍼스트레벨 캐시에 있는지만 확인한다.
     */
    private boolean isInsertOperation(Class<?> clazz, Long id) {
        return id == null || persistenceContext.getEntity(clazz, id) == null;
    }

    @Override
    public void remove(Object entity) {
        if (persistenceContext.isRemoved(entity)) {
            // 아무것도 안함
            return;
        }
        Class<?> clazz = entity.getClass();
        Long id = getRowId(entity);
        find(clazz, id);
        entityPersister.delete(clazz, id);
        persistenceContext.removeEntity(entity);
    }

    private static Long getRowId(Object entity) {
        Class<?> clazz = entity.getClass();
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        return entityMetadata.getPrimaryKeyValue(entity);
    }
}
