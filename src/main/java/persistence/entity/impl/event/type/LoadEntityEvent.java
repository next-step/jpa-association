package persistence.entity.impl.event.type;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class LoadEntityEvent implements EntityEvent {

    private final Object id;
    private final Class<?> clazz;
    private final EventSource eventSource;

    private LoadEntityEvent(EventSource eventSource, Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.eventSource = eventSource;
        this.id = id;
    }

    public static LoadEntityEvent of(Class<?> clazz, Object id, EventSource eventSource) {
        return new LoadEntityEvent(eventSource, clazz, id);
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
    }

    @Override
    public Object getEntity() {
        return null;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getId() {
        return id;
    }
}
