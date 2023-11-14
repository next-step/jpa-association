package persistence.entity.impl.event;

import persistence.entity.EventSource;

public interface EntityEventListener {

    void onEvent(EntityEvent entityEvent);

    <T> T onEvent(Class<T> clazz, EntityEvent entityEvent);

    void syncPersistenceContext(EventSource eventSource, Object entity);
}
