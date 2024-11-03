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
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;


/*
- Persist로 Person 저장 후 영속성 컨텍스트에 존재하는지 확인한다.
- remove 실행하면 영속성 컨텍스트에 데이터가 제거된다.
- update 실행하면 영속성컨텍스트 데이터도 수정된다.
*/
class EntityLoaderTest {

    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private H2DBConnection h2DBConnection;
    private JdbcTemplate jdbcTemplate;
    private PersistenceContext persistenceContext;

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

        this.persistenceContext = new PersistenceContextImpl();

        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.entityLoader = new EntityLoader(jdbcTemplate);
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

    @DisplayName("Persist로 Person 저장 후 조회하여 확인한다.")
    @Test
    void findTest() {
        Person person = createPerson(1);
        this.entityPersister.persist(EntityData.createEntityData(person));

        assertThat(this.entityLoader.find(Person.class, 1L))
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "test@test.com");
    }

    @DisplayName("Persist로 Order와 OrderItem을 저장 후 조회한다.")
    @Test
    void findOrderTest() {
        Order order = new Order(1L, "1234", List.of(createOrderItem(1, 1L)));
        this.entityPersister.persist(EntityData.createEntityData(order));

        Order findOrder = this.entityLoader.find(Order.class, 1L);

        assertThat(findOrder)
                .extracting("id", "orderNumber")
                .contains(1L, "1234");
        assertThat(findOrder.getOrderItems())
                .extracting("id", "orderId", "product", "quantity")
                .containsExactly(tuple(1L, 1L, "테스트상품1", 1));
    }

    private Person createPerson(int i) {
        return new Person((long) i, "test" + i, 29, "test@test.com");
    }

    private OrderItem createOrderItem(int i, Long orderId) {
        return new OrderItem((long) i, orderId, "테스트상품"+i, 1);
    }
}
