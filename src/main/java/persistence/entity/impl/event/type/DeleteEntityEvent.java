package persistence.entity.impl.event.type;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class DeleteEntityEvent implements EntityEvent {

    private final Class<?> clazz;
    private final Object entity;
    private final EventSource eventSource;

    private DeleteEntityEvent(Class<?> clazz, Object entity, EventSource eventSource) {
        this.clazz = clazz;
        this.entity = entity;
        this.eventSource = eventSource;
    }

    public static DeleteEntityEvent of(Object entity, EventSource eventSource) {
        return new DeleteEntityEvent(entity.getClass(), entity, eventSource);
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
}
