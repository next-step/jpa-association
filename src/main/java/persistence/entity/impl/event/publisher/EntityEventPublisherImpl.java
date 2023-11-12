package persistence.entity.impl.event.publisher;

import persistence.entity.impl.event.EntityEventPublisher;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.type.DeleteEntityEvent;
import persistence.entity.impl.event.type.LoadEntityEvent;
import persistence.entity.impl.event.type.MergeEntityEvent;
import persistence.entity.impl.event.type.PersistEntityEvent;

public class EntityEventPublisherImpl implements EntityEventPublisher {

    private final EntityEventDispatcher entityEventDispatcher;

    public EntityEventPublisherImpl(EntityEventDispatcher entityEventDispatcher) {
        this.entityEventDispatcher = entityEventDispatcher;
    }

    @Override
    public Object onLoad(LoadEntityEvent event) {
        return entityEventDispatcher.handle(event.getClazz(), event);
    }

    @Override
    public Object onPersist(PersistEntityEvent event) {
        return entityEventDispatcher.handle(event.getClazz(), event);
    }

    @Override
    public Object onMerge(MergeEntityEvent event) {
        return entityEventDispatcher.handle(event.getClazz(), event);
    }

    @Override
    public void onDelete(DeleteEntityEvent event) {
        entityEventDispatcher.handle(event);
    }
}
