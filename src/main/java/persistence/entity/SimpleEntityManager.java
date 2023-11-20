package persistence.entity;


import java.util.List;
import persistence.entity.persister.EntityPersister;


public class SimpleEntityManager implements EntityManager {
    private final EntityPersisteContext entityPersisterContenxt;
    private final SimplePersistenceContext persistenceContext;

    private SimpleEntityManager(EntityPersisteContext entityPersisterContenxt) {
        this.persistenceContext = new SimplePersistenceContext();
        this.entityPersisterContenxt = entityPersisterContenxt;
    }

    public static SimpleEntityManager create(EntityPersisteContext entityPersisterContenxt) {
        return new SimpleEntityManager(entityPersisterContenxt);
    }

    @Override
    public <T> T persist(T entity) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(entity.getClass());

        return persistenceContext.saving(entityPersister, entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.deleted(entity);
    }

    @Override
    public <T, ID> T find(Class<T> clazz, ID id) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(clazz);

        return persistenceContext.loading(entityPersister, clazz, id);
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(tClass);

        return persistenceContext.findAll(entityPersister, tClass);
    }

    @Override
    public void flush() {
        persistenceContext.flush(entityPersisterContenxt);
    }

}
