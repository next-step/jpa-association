package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.context.BasicPersistentContext;
import persistence.context.PersistenceContext;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;

import java.util.List;

public class BasicEntityManger implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;
    private final EntityLoader entityLoader;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;
    private final EntityMetaFactory entityMetaFactory = EntityMetaFactory.INSTANCE;

    public BasicEntityManger(JdbcTemplate jdbcTemplate, EntityLoader entityLoader) {
        this.persistenceContext = new BasicPersistentContext();
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object entity = persistenceContext.getEntity(id);
        if (entity != null) {
            return (T) entity;
        }

        T selectedEntity = entityLoader.load(clazz, id);
        if (selectedEntity == null) {
            return null;
        }

        persistenceContext.addEntity(id, selectedEntity);
        persistenceContext.getDatabaseSnapshot(id, selectedEntity);
        return selectedEntity;
    }

    @Override
    public void persist(Object entity) {
        EntityMeta entityMeta = entityMetaFactory.create(entity.getClass());
        String insertQuery = insertQueryBuilder.insert(entityMeta, entity);
        Long id = jdbcTemplate.insert(insertQuery);
        entityMeta.getIdColumn().setValue(entity, id);
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

    @Override
    public void remove(Object entity) {
        EntityMeta entityMeta = entityMetaFactory.create(entity.getClass());
        String deleteQuery = deleteQueryBuilder.delete(entityMeta, entity);
        jdbcTemplate.execute(deleteQuery);
        removePersistentContextEntity(entityMeta, entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntityMeta entityMeta = entityMetaFactory.create(entity.getClass());
        Long id = (Long) entityMeta.getIdColumn().getValue(entity);
        T originEntity = originEntity(entity.getClass(), id);

        if (hasUpdatedField(entityMeta, originEntity, entity)) {
            String updateQuery = updateQueryBuilder.update(entityMeta, entity);
            jdbcTemplate.execute(updateQuery);
            persistenceContext.getDatabaseSnapshot(id, entity);
            persistenceContext.addEntity(id, entity);
        }

        return entity;
    }

    private <T> T originEntity(Class<?> clazz, Long id) {
        Object snapshot = persistenceContext.getCachedDatabaseSnapshot(id);

        if (snapshot != null) {
            return (T) snapshot;
        }

        return (T) find(clazz, id);
    }

    private <T> boolean hasUpdatedField(EntityMeta entityMeta, T snapShot, T entity) {
        List<Object> snapShotValues = entityMeta.getNormalColumns().getValues(snapShot);
        List<Object> entityValues = entityMeta.getNormalColumns().getValues(entity);

        return !snapShotValues.equals(entityValues);
    }

    private void removePersistentContextEntity(EntityMeta entityMeta, Object entity) {
        persistenceContext.removeEntity((Long) entityMeta.getIdColumn().getValue(entity));
    }
}
