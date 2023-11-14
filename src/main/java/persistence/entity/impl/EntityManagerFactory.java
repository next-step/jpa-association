package persistence.entity.impl;

import java.sql.Connection;
import persistence.entity.EntityManager;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.dispatcher.EntityEventDispatcherImpl;
import persistence.entity.impl.event.listener.DeleteEntityEventListenerImpl;
import persistence.entity.impl.event.listener.LoadEntityEventListenerImpl;
import persistence.entity.impl.event.listener.MergeEntityEventListenerImpl;
import persistence.entity.impl.event.listener.PersistEntityEventListenerImpl;
import persistence.entity.impl.event.publisher.EntityEventPublisherImpl;
import persistence.entity.impl.retrieve.EntityLoader;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersister;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.dialect.ColumnType;

/**
 * EntityManagerFactory는 JPA의 EntityManagerFactory 인터페이스를 구현예정입니다.
 */
public class EntityManagerFactory {

    private final Connection connection;
    private final ColumnType columnType;

    public EntityManagerFactory(Connection connection, ColumnType columnType) {
        this.connection = connection;
        this.columnType = columnType;
    }

    public EntityManager createEntityManager() {
        final EntityEventDispatcher entityEventDispatcher = initEventDispatcher(connection);
        final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext(columnType);

        return new EntityManagerImpl(connection, columnType, persistenceContext, initEventPublisher(entityEventDispatcher));
    }

    private EntityEventPublisherImpl initEventPublisher(EntityEventDispatcher entityEventDispatcher) {
        return new EntityEventPublisherImpl(entityEventDispatcher);
    }

    private EntityEventDispatcherImpl initEventDispatcher(Connection connection) {
        final EntityLoader entityLoader = new EntityLoaderImpl(connection);
        final EntityPersister entityPersister = new EntityPersisterImpl(connection);

        return new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(entityLoader, columnType),
            new MergeEntityEventListenerImpl(entityPersister, columnType),
            new PersistEntityEventListenerImpl(entityPersister, columnType),
            new DeleteEntityEventListenerImpl(entityPersister, columnType)
        );
    }

}
