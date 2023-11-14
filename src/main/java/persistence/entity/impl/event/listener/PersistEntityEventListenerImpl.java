package persistence.entity.impl.event.listener;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.store.EntityPersister;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class PersistEntityEventListenerImpl implements EntityEventListener {

    private final EntityPersister entityPersister;
    private final ColumnType columnType;

    public PersistEntityEventListenerImpl(EntityPersister entityPersister, ColumnType columnType) {
        this.entityPersister = entityPersister;
        this.columnType = columnType;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        throw new RuntimeException("PersistEventListener는 반환값이 있는 이벤트만 처리할 수 있습니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        final Object savedEntity = entityPersister.store(entityEvent.getEntity(), columnType);
        final EventSource eventSource = entityEvent.getEventSource();
        eventSource.saving(savedEntity);

        syncPersistenceContext(eventSource, savedEntity);
        return clazz.cast(savedEntity);
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        final EntityObjectMappingMeta savedObjectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        eventSource.putEntity(savedObjectMappingMeta.getIdValue(), entity);
    }
}
