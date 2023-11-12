package persistence.entity.impl.event.dispatcher;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.exception.UnknownEventType;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.event.type.DeleteEntityEvent;
import persistence.entity.impl.event.type.LoadEntityEvent;
import persistence.entity.impl.event.type.MergeEntityEvent;
import persistence.entity.impl.event.type.PersistEntityEvent;

public class EntityEventDispatcherImpl implements EntityEventDispatcher {

    private final Map<Class<?>, EntityEventListener> eventListenerMap = new HashMap<>();

    public EntityEventDispatcherImpl(
        EntityEventListener loadEntityEventListener,
        EntityEventListener mergeEntityEventListener,
        EntityEventListener persistEntityEventListener,
        EntityEventListener deleteEntityEventListener
    ) {
        eventListenerMap.put(LoadEntityEvent.class, loadEntityEventListener);
        eventListenerMap.put(MergeEntityEvent.class, mergeEntityEventListener);
        eventListenerMap.put(PersistEntityEvent.class, persistEntityEventListener);
        eventListenerMap.put(DeleteEntityEvent.class, deleteEntityEventListener);
    }

    @Override
    public void handle(EntityEvent entityEvent) {
        final EntityEventListener entityEventListener = eventListenerMap.get(entityEvent.getClass());
        if (entityEventListener == null) {
            throw new UnknownEventType(String.format("%s 타입에 대해 알 수 없습니다.", entityEvent.getClass().getSimpleName()));
        }
        entityEventListener.onEvent(entityEvent);
    }

    @Override
    public <T> T handle(Class<T> clazz, EntityEvent entityEvent) {
        final EntityEventListener entityEventListener = eventListenerMap.get(entityEvent.getClass());
        if (entityEventListener == null) {
            throw new UnknownEventType(String.format("%s 타입에 대해 알 수 없습니다.", entityEvent.getClass().getSimpleName()));
        }
        return entityEventListener.onEvent(clazz, entityEvent);
    }
}
