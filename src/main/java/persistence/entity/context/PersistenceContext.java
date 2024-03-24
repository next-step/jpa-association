package persistence.entity.context;

import persistence.entity.context.EntityKey;

public interface PersistenceContext {

    Object getEntity(EntityKey id);

    void addEntity(EntityKey id, Object entity);

    void removeEntity(Object entity);

    boolean isDirty(EntityKey id, Object entity);
}
