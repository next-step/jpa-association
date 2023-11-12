package persistence.entity.impl.event;

import persistence.entity.impl.event.type.DeleteEntityEvent;
import persistence.entity.impl.event.type.LoadEntityEvent;
import persistence.entity.impl.event.type.MergeEntityEvent;
import persistence.entity.impl.event.type.PersistEntityEvent;

public interface EntityEventPublisher {

    Object onLoad(LoadEntityEvent event);

    Object onMerge(MergeEntityEvent event);

    Object onPersist(PersistEntityEvent event);

    void onDelete(DeleteEntityEvent event);
}
