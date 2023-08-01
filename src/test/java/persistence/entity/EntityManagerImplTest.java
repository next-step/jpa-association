package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Order;
import domain.OrderFixture;
import domain.OrderItem;
import domain.Person;
import domain.PersonFixture;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DdlBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.DmlBuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EntityManagerImplTest {
    private static final DdlBuilder ddl = Dialect.H2.getDdl();
    private static final DmlBuilder dml = Dialect.H2.getDml();
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;
    private Person person;
    private Object personId;

    @BeforeAll
    static void beforeAll() {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @BeforeEach
    void setUp() {
        List.of(
                ddl.getDropQuery(Person.class),
                ddl.getDropQuery(OrderItem.class),
                ddl.getDropQuery(Order.class),
                ddl.getCreateQuery(Order.class),
                ddl.getCreateQuery(OrderItem.class),
                ddl.getCreateQuery(Person.class)
        ).forEach(jdbcTemplate::execute);
        jdbcTemplate.execute(OrderFixture.INSERT_ORDERS);
        jdbcTemplate.execute(OrderFixture.INSERT_ORDER_ITEMS);
        entityManager = new EntityManagerImpl(
                new StatefulPersistenceContext(),
                jdbcTemplate, dml
        );
        person = PersonFixture.createPerson();
        personId = new EntityKey<>(person).getEntityId();
    }

    @Test
    @DisplayName("persist 를 통해 엔티티를 영속화 할 수 있다.")
    void persist() {
        entityManager.persist(person);
        assertThat(
                entityManager.find(
                        person.getClass(),
                        personId
                ).get() == person
        ).isTrue();
    }

    @Test
    @DisplayName("remove 를 통해 엔티티를 제거할 수 있다.")
    void remove() {
        entityManager.persist(person);
        entityManager.remove(person);
        assertThat(
                entityManager.findAll(
                        person.getClass()
                ).isEmpty()
        ).isTrue();
    }

    @Test
    @DisplayName("isDirty 를 통해 상태가 변한 엔티티를 확인할 수 있다.")
    void dirtyCheck() {
        final String CHANGED_NAME = "Changed Name";
        entityManager.persist(person);
        person.setName(CHANGED_NAME);
        assertThat(
                entityManager.isDirty(person)
        ).isTrue();
    }

    @Test
    @DisplayName("OneToMany 관계에 있는 엔티티를 객체화 할 수 있다.")
    void oneToMany() {
        List<Order> orders = entityManager.findAll(Order.class);
        List<OrderItem> orderItems = orders.stream()
                .map(Order::getOrderItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(orderItems).isNotEmpty();
    }
}
