package persistence.entity.manager;

import jakarta.persistence.OneToMany;
import jakarta.persistence.criteria.Join;
import jdbc.JdbcTemplate;
import persistence.PrimaryKey;
import persistence.entity.exception.EntityExistsException;
import persistence.entity.exception.NoOneToManyAssociationException;
import persistence.entity.exception.ReadOnlyException;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persistencecontext.EntityEntry;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.clause.column.JoinClause;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;


    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl();
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    public EntityManagerImpl(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        Optional<T> cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity.isPresent()) {
            return cachedEntity;
        }

        Optional<T> searchedEntity = entityLoader.find(clazz, id);
        if (searchedEntity.isEmpty()) {
            return Optional.empty();
        }
        if (JoinClause.hasOneToMany(searchedEntity.get().getClass())) {
            mapChildEntities(searchedEntity.get());
        }
        EntityEntry entityEntry = persistenceContext.getEntityEntry(clazz, id).get();
        entityEntry.load();
        T addedEntity = persistenceContext.addEntity(searchedEntity.get(), id);
        entityEntry.finishStatusUpdate();
        return Optional.of(addedEntity);
    }

    private <T> void mapChildEntities(T searchedEntity) {
        Class<?> childEntityClass = JoinClause.childEntityClass(searchedEntity.getClass());
        List<?> childEntities = entityLoader.findAll(childEntityClass);

        Field childEntityField = JoinClause.findChildEntityField(searchedEntity.getClass());
        childEntityField.setAccessible(true);
        try {
            childEntityField.set(searchedEntity, childEntities);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("OneToMany 매핑에 실패하였습니다.");
        }
    }

    @Override
    public <T> T persist(T entity) {
        validate(entity);
        T insertedEntity = entityPersister.insert(entity);
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.save();
        entityEntry.finishStatusUpdate();
        return persistenceContext.updateEntity(insertedEntity, new PrimaryKey(insertedEntity).getPrimaryKeyValue(entity));
    }

    private void validate(Object entity) {
        Long primaryKey = new PrimaryKey(entity).getPrimaryKeyValue(entity);

        Optional<?> searchedEntityEntry = persistenceContext.getEntityEntry(entity.getClass(), primaryKey);
        if (searchedEntityEntry.isPresent()) {
            throw new EntityExistsException();
        }
    }

    @Override
    public <T> T merge(T entity) {
        // TODO oneToMany 관계

        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new ReadOnlyException();
        }

        Long primaryKey = new PrimaryKey(entity).getPrimaryKeyValue(entity);

        if (persistenceContext.isDirty(entity)) {
            entityEntry.save();
            T updatedEntity = entityPersister.update(entity, primaryKey);
            T result = persistenceContext.updateEntity(updatedEntity, primaryKey);
            entityEntry.finishStatusUpdate();
            return result;
        }
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.removeFromPersistenceContext();
        entityPersister.delete(entity);
        entityEntry.removeFromDatabase();
    }
}
