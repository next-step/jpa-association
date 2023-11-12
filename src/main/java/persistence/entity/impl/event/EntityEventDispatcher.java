package persistence.entity.impl.event;

public interface EntityEventDispatcher {

    void handle(EntityEvent entityEvent);

    <T> T handle(Class<T> eventType, EntityEvent entityEvent);
}
