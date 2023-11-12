package persistence.entity.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.impl.event.EntityEventPublisher;
import persistence.entity.EntityManager;
import persistence.entity.EventSource;
import persistence.entity.PersistenceContext;
import persistence.entity.impl.event.type.DeleteEntityEvent;
import persistence.entity.impl.event.type.LoadEntityEvent;
import persistence.entity.impl.event.type.MergeEntityEvent;
import persistence.entity.impl.event.type.PersistEntityEvent;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class EntityManagerImpl implements EntityManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ColumnType columnType;
    private final Connection connection;
    private final PersistenceContext persistenceContext;
    private final EntityEventPublisher entityEventPublisher;

    public EntityManagerImpl(Connection connection, ColumnType columnType, PersistenceContext persistenceContext, EntityEventPublisher eventPublisher) {
        this.connection = connection;
        this.columnType = columnType;
        this.entityEventPublisher = eventPublisher;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final Optional<Object> cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity.isEmpty()) {
            final Object loadedEntity = entityEventPublisher.onLoad(LoadEntityEvent.of(clazz, id, (EventSource)persistenceContext));
            return clazz.cast(loadedEntity);
        }

        return clazz.cast(cachedEntity.get());
    }

    @Override
    public Object persist(Object entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        final Optional<Object> cachedEntity = persistenceContext.getEntity(entity.getClass(), objectMappingMeta.getIdValue());

        return cachedEntity.orElseGet(() ->
            entityEventPublisher.onPersist(PersistEntityEvent.of(entity, (EventSource)persistenceContext))
        );
    }

    @Override
    public void remove(Object entity) {
        entityEventPublisher.onDelete(DeleteEntityEvent.of(entity, (EventSource)persistenceContext));
    }

    @Override
    public <T> T merge(Class<T> clazz, T entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        final SnapShot snapShot = persistenceContext.getSnapShot(entity.getClass(), objectMappingMeta.getIdValue());
        if (snapShot.isSameWith(objectMappingMeta)) {
            return entity;
        }

        final Object mergedEntity = entityEventPublisher.onMerge(MergeEntityEvent.of(entity, (EventSource)persistenceContext));

        return clazz.cast(mergedEntity);
    }

    @Override
    public void clear() {
        persistenceContext.clearContextCache();
    }

    @Override
    public void close() {
        try {
            if (this.connection == null) {
                return;
            }

            if (this.connection.isClosed()) {
                return;
            }

            this.clear();
            this.connection.close();
            log.info("EntityManager closed");
        } catch (SQLException e) {
            log.error("EntityManager connection not closed", e);
            throw new RuntimeException(e);
        }
    }
}
