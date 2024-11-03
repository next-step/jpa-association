package persistence;

import builder.ddl.DDLBuilderData;
import builder.ddl.builder.CreateQueryBuilder;
import builder.ddl.builder.DropQueryBuilder;
import builder.ddl.dataType.DB;
import builder.dml.EntityData;
import database.H2DBConnection;
import entity.Order;
import entity.OrderItem;
import entity.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EntityPersisterTest {

    private EntityLoader entityLoader;
    private EntityPersister entityPersister;
    private H2DBConnection h2DBConnection;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        this.h2DBConnection = new H2DBConnection();
        this.jdbcTemplate = this.h2DBConnection.start();

        //테이블 생성
        CreateQueryBuilder queryBuilder = new CreateQueryBuilder();
        String personCreateQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Person.class, DB.H2));
        String OrderCreateQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Order.class, DB.H2));
        String OrderItemCreateQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(OrderItem.class, DB.H2));

        jdbcTemplate.execute(personCreateQuery);
        jdbcTemplate.execute(OrderCreateQuery);
        jdbcTemplate.execute(OrderItemCreateQuery);

        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    //정확한 테스트를 위해 메소드마다 테이블 DROP 후 DB종료
    @AfterEach
    void tearDown() {
        DropQueryBuilder queryBuilder = new DropQueryBuilder();
        String personDropQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Person.class, DB.H2));
        String orderDropQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(Order.class, DB.H2));
        String orderItemDropQuery = queryBuilder.buildQuery(DDLBuilderData.createDDLBuilderData(OrderItem.class, DB.H2));
        jdbcTemplate.execute(personDropQuery);
        jdbcTemplate.execute(orderDropQuery);
        jdbcTemplate.execute(orderItemDropQuery);
        this.h2DBConnection.stop();
    }

    @DisplayName("Persist로 Person 저장한다.")
    @Test
    void findTest() {
        Person person = createPerson(1);
        this.entityPersister.persist(EntityData.createEntityData(person));

        Person findPerson = this.entityLoader.find(Person.class, person.getId());

        assertThat(findPerson)
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "test@test.com");
    }

    @DisplayName("remove 실행한다.")
    @Test
    void removeTest() {
        Person person = createPerson(1);
        this.entityPersister.persist(EntityData.createEntityData(person));
        this.entityPersister.remove(EntityData.createEntityData(person));

        assertThatThrownBy(() -> this.entityLoader.find(Person.class, person.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected 1 result, got 0");
    }

    @DisplayName("merge 실행한다.")
    @Test
    void updateTest() {
        Person person = createPerson(1);
        this.entityPersister.persist(EntityData.createEntityData(person));

        person.changeEmail("changed@test.com");
        this.entityPersister.merge(EntityData.createEntityData(person));

        Person findPerson = this.entityLoader.find(Person.class, person.getId());

        assertThat(findPerson)
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "changed@test.com");
    }

    @DisplayName("Join되어있는 Entity도 insert를 실행한다.")
    @Test
    void joinInsertTest() {
        Order order = new Order(1L, "1234", List.of(createOrderItem(1, 1L)));
        this.entityPersister.persist(EntityData.createEntityData(order));

    }

    private Person createPerson(int i) {
        return new Person((long) i, "test" + i, 29, "test@test.com");
    }

    private OrderItem createOrderItem(int i, Long orderId) {
        return new OrderItem((long) i, orderId, "테스트상품"+i, 1);
    }

}
