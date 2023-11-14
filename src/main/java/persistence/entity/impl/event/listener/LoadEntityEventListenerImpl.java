package persistence.entity.impl.event.listener;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.retrieve.EntityLoader;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class LoadEntityEventListenerImpl implements EntityEventListener {

    private final EntityLoader entityLoader;
    private final ColumnType columnType;

    public LoadEntityEventListenerImpl(EntityLoader entityLoader, ColumnType columnType) {
        this.entityLoader = entityLoader;
        this.columnType = columnType;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        throw new RuntimeException("LoadEvent는 반환값이 항상 존재합니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        final T loadedEntity = entityLoader.load(clazz, entityEvent.getId(), columnType);
        final EventSource eventSource = entityEvent.getEventSource();
        eventSource.loading(loadedEntity);

        syncPersistenceContext(eventSource, loadedEntity);
        return loadedEntity;
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        final EntityObjectMappingMeta savedObjectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        eventSource.putEntity(savedObjectMappingMeta.getIdValue(), entity);
    }
}
