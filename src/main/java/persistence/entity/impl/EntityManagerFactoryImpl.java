package persistence.entity.impl;

import java.sql.Connection;
import persistence.entity.EntityManager;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.dispatcher.EntityEventDispatcherImpl;
import persistence.entity.impl.event.listener.DeleteEntityEventListenerImpl;
import persistence.entity.impl.event.listener.LoadEntityEventListenerImpl;
import persistence.entity.impl.event.listener.MergeEntityEventListenerImpl;
import persistence.entity.impl.event.listener.PersistEntityEventListenerImpl;
import persistence.entity.impl.event.publisher.EntityEventPublisherImpl;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.dialect.ColumnType;

public class EntityManagerFactoryImpl {

    private final Connection connection;
    private final ColumnType columnType;

    public EntityManagerFactoryImpl(Connection connection, ColumnType columnType) {
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
        final EntityLoaderImpl entityLoader = new EntityLoaderImpl(connection);
        final EntityPersisterImpl entityPersister = new EntityPersisterImpl(connection);

        return new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(entityLoader, columnType),
            new MergeEntityEventListenerImpl(entityPersister, columnType),
            new PersistEntityEventListenerImpl(entityPersister, columnType),
            new DeleteEntityEventListenerImpl(entityPersister, columnType)
        );
    }

}
