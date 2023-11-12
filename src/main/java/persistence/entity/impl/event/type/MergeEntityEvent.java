package persistence.entity.impl.event.type;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class MergeEntityEvent implements EntityEvent {

    private final Class<?> clazz;
    private final Object entity;
    private final EventSource eventSource;

    private MergeEntityEvent(Class<?> clazz, Object entity, EventSource eventSource) {
        this.clazz = clazz;
        this.entity = entity;
        this.eventSource = eventSource;
    }

    public static MergeEntityEvent of(Object entity, EventSource eventSource) {
        return new MergeEntityEvent(entity.getClass(), entity, eventSource);
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public Object getId() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
