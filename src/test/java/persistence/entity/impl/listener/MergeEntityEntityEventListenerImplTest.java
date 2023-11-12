package persistence.entity.impl.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.EventSource;
import persistence.entity.impl.EntityManagerImpl;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.type.MergeEntityEvent;
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

@DisplayName("MergeEventListener 테스트")
class MergeEntityEntityEventListenerImplTest {
    private DatabaseServer server;

    private Database jdbcTemplate;

    private EventSource eventSource;

    private EntityEventListener mergeEntityEventListener;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        Connection connection = server.getConnection();

        final EntityPersisterImpl persister = new EntityPersisterImpl(connection);
        final EntityLoaderImpl loader = new EntityLoaderImpl(connection);
        final H2ColumnType columnType = new H2ColumnType();
        mergeEntityEventListener = new MergeEntityEventListenerImpl(persister, columnType);
        final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext(columnType);
        eventSource = persistenceContext;

        EntityEventDispatcher entityEventDispatcher = new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(loader, columnType),
            mergeEntityEventListener,
            new PersistEntityEventListenerImpl(persister, columnType),
            new DeleteEntityEventListenerImpl(persister, columnType)
        );
        EntityEventPublisher entityEventPublisher = new EntityEventPublisherImpl(entityEventDispatcher);

        entityManager = new EntityManagerImpl(connection, columnType, persistenceContext, entityEventPublisher);
        jdbcTemplate = new JdbcTemplate(connection);
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator(columnType);
        jdbcTemplate.execute(createDDLQueryGenerator.create(MergeEventEntity.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator(new H2ColumnType());
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(MergeEventEntity.class));
        entityManager.close();
        server.stop();
    }

    @Test
    @DisplayName("병합 이벤트를 수신하면 병합 엔티티의 내용이 반영된다.")
    void mergeEvent() {
        // given
        final MergeEventEntity mergeEventEntity = new MergeEventEntity(null, "saved");
        final Object savedEntity = entityManager.persist(mergeEventEntity);
        final MergeEventEntity savedMergeEventEntity = (MergeEventEntity) savedEntity;
        savedMergeEventEntity.setName("merged");

        final MergeEntityEvent mergeEvent = MergeEntityEvent.of(savedMergeEventEntity, eventSource);

        // when
        final MergeEventEntity mergeEventResultEvent = mergeEntityEventListener.onEvent(MergeEventEntity.class, mergeEvent);

        // then
        final MergeEventEntity foundEntity = entityManager.find(MergeEventEntity.class, 1L);
        assertAll(
            () -> assertThat(foundEntity).isNotNull(),
            () -> assertThat(mergeEventResultEvent.getId()).isEqualTo(foundEntity.getId()),
            () -> assertThat(mergeEventResultEvent.getName()).isEqualTo(foundEntity.getName())
        );
    }

    @Test
    @DisplayName("READ ONLY 상태의 엔티티에 병합 이벤트를 수신하면 변경된 엔티티의 내용이 반영되지 않는다.")
    void cannotMergeReadOnlyEntity() {
        // given
        final MergeEventEntity mergeEventEntity = new MergeEventEntity(null, "saved");
        final Object savedEntity = entityManager.persist(mergeEventEntity);
        final MergeEventEntity savedMergeEventEntity = (MergeEventEntity) savedEntity;
        savedMergeEventEntity.setName("merged");

        final MergeEntityEvent mergeEvent = MergeEntityEvent.of(savedMergeEventEntity, eventSource);

        // when
        eventSource.readOnly(savedMergeEventEntity);

        // then
        assertThatThrownBy(
            () -> mergeEntityEventListener.onEvent(MergeEventEntity.class, mergeEvent)
        ).isInstanceOf(RuntimeException.class)
            .hasMessage("해당 Entity는 변경될 수 없습니다.");
    }

    @Entity
    static class MergeEventEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public MergeEventEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        protected MergeEventEntity() {
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}