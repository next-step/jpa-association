package persistence.entity.impl.event.type;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class PersistEntityEvent implements EntityEvent {

    private final Class<?> clazz;
    private final Object entity;
    private final EventSource eventSource;

    private PersistEntityEvent(Class<?> clazz, Object entity, EventSource eventSource) {
        this.clazz = clazz;
        this.entity = entity;
        this.eventSource = eventSource;
    }

    public static PersistEntityEvent of(Object entity, EventSource eventSource) {
        return new PersistEntityEvent(entity.getClass(), entity, eventSource);
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public Object getId() {
        throw new UnsupportedOperationException();
    }
}
