package persistence.entity;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.common.AccessibleField;
import persistence.common.Fields;
import persistence.context.BasicPersistentContext;
import persistence.context.PersistenceContext;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;
import persistence.sql.dml.column.DmlColumns;

public class BasicEntityManger implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;
    private final EntityLoader entityLoader;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;
    private final UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.INSTANCE;

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
        String insertQuery = insertQueryBuilder.insert(entity);
        Long id = jdbcTemplate.insert(insertQuery);
        setGeneratedId(entity, id);
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

    private void setGeneratedId(Object entity, Long id) {
        AccessibleField idField = getAccessibleField(entity);
        idField.setValue(entity, id);
    }

    @Override
    public void remove(Object entity) {
        String deleteQuery = deleteQueryBuilder.delete(entity);
        jdbcTemplate.execute(deleteQuery);
        removePersistentContextEntity(entity);
    }

    @Override
    public <T> T merge(T entity) {
        AccessibleField accessibleIdField = getAccessibleField(entity);
        Long id = (Long) accessibleIdField.getValue(entity);
        T originEntity = originEntity(entity.getClass(), id);

        if (hasUpdatedField(originEntity, entity)) {
            String updateQuery = updateQueryBuilder.update(entity);
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

    private <T> boolean hasUpdatedField(T snapShot, T entity) {
        DmlColumns snapShotEntityColumns = DmlColumns.of(snapShot);
        DmlColumns entityColumns = DmlColumns.of(entity);

        return !snapShotEntityColumns.equals(entityColumns);
    }

    private void removePersistentContextEntity(Object entity) {
        AccessibleField idField = getAccessibleField(entity);
        persistenceContext.removeEntity((Long) idField.getValue(entity));
    }

    private static AccessibleField getAccessibleField(Object entity) {
        Fields fields = Fields.of(entity.getClass());
        return fields.getAccessibleField(Id.class);
    }
}
