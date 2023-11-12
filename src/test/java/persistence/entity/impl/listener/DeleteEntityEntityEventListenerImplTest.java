package persistence.entity.impl.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.impl.event.EntityEventPublisher;
import persistence.entity.EntityManager;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.EventSource;
import persistence.entity.impl.EntityManagerImpl;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.type.DeleteEntityEvent;
import persistence.entity.impl.event.dispatcher.EntityEventDispatcherImpl;
import persistence.entity.impl.event.listener.DeleteEntityEventListenerImpl;
import persistence.entity.impl.event.listener.LoadEntityEventListenerImpl;
import persistence.entity.impl.event.listener.MergeEntityEventListenerImpl;
import persistence.entity.impl.event.listener.PersistEntityEventListenerImpl;
import persistence.entity.impl.event.publisher.EntityEventPublisherImpl;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;

@DisplayName("DeleteEventListener 통합 테스트")
class DeleteEntityEntityEventListenerImplTest {
    private DatabaseServer server;

    private Database jdbcTemplate;

    private EventSource eventSource;

    private EntityEventListener deleteEntityEventListener;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        Connection connection = server.getConnection();
        final H2ColumnType columnType = new H2ColumnType();

        final EntityPersisterImpl persister = new EntityPersisterImpl(connection);
        final EntityLoaderImpl loader = new EntityLoaderImpl(connection);
        deleteEntityEventListener = new DeleteEntityEventListenerImpl(persister, columnType);
        final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext(columnType);
        eventSource = persistenceContext;

        EntityEventDispatcher entityEventDispatcher = new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(loader, columnType),
            new MergeEntityEventListenerImpl(persister, columnType),
            new PersistEntityEventListenerImpl(persister, columnType),
            deleteEntityEventListener
        );
        EntityEventPublisher entityEventPublisher = new EntityEventPublisherImpl(entityEventDispatcher);

        entityManager = new EntityManagerImpl(connection, columnType, persistenceContext, entityEventPublisher);
        jdbcTemplate = new JdbcTemplate(connection);
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator(columnType);
        jdbcTemplate.execute(createDDLQueryGenerator.create(DeleteEventEntity.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator(new H2ColumnType());
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(DeleteEventEntity.class));
        entityManager.close();
        server.stop();
    }

    @Test
    @DisplayName("삭제 이벤트를 수신하면 해당 엔티티는 삭제된다.")
    void deleteEvent() {
        // given
        final DeleteEventEntity deleteEventEntity = new DeleteEventEntity();
        final Object savedEntity = entityManager.persist(deleteEventEntity);

        EntityEvent deleteEntityEvent = DeleteEntityEvent.of(savedEntity, eventSource);

        // expect
        assertThatCode(() -> deleteEntityEventListener.onEvent(deleteEntityEvent))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("READ_ONLY 상태의 엔티티에 대해 삭제 이벤트를 수신하면 해당 엔티티는 삭제되지 않는다.")
    void cannotDeleteReadOnlyStatus() {
        // given
        final DeleteEventEntity deleteEventEntity = new DeleteEventEntity();
        final Object savedEntity = entityManager.persist(deleteEventEntity);

        EntityEvent deleteEntityEvent = DeleteEntityEvent.of(savedEntity, eventSource);

        // when
        eventSource.readOnly(savedEntity);

        // then
        assertThatThrownBy(() -> deleteEntityEventListener.onEvent(deleteEntityEvent))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("해당 Entity는 삭제될 수 없습니다.");
    }

    @Entity
    static class DeleteEventEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public DeleteEventEntity(Long id) {
            this.id = id;
        }

        protected DeleteEventEntity() {
        }

        public Long getId() {
            return id;
        }
    }
}