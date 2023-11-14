package persistence.entity.impl.event;

import persistence.entity.EventSource;

public interface EntityEvent {

    EventSource getEventSource();

    Object getEntity();

    Object getId();

}
