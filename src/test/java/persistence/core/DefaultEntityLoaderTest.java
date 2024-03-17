package persistence.core;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Order;
import persistence.entity.OrderItem;
import persistence.entity.Person;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityLoaderTest {

    DatabaseServer server;
    EntityPersister entityPersister;
    EntityLoader entityLoader;
    DDLExcuteor ddlExcuteor;

    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlExcuteor = new DDLExcuteor(jdbcTemplate);
        entityPersister = new DefaultEntityPersister(jdbcTemplate);
        entityLoader = new DefaultEntityLoader(jdbcTemplate);

        createTable();
        insertSampleData(2);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable();
        server.stop();
    }

    @Test
    @DisplayName("findById Test")
    public void findTest() throws Exception {

        Person select = entityLoader.find(Person.class, 2L);

        assertAll(
                () -> assertNotNull(select),
                () -> assertEquals(select.getId(), 2L),
                () -> assertEquals(select.getName(), "jinny_1"),
                () -> assertEquals(select.getAge(), 31)
        );
    }

    @Test
    @DisplayName("find with RelationEntity Test")
    public void findRelationEntityTest() {
        Order order = entityLoader.find(Order.class, 1L);
        List<OrderItem> orderItems = order.getOrderItems();

        assertAll(
                () -> assertNotNull(orderItems),
                () -> assertEquals(orderItems.size(), 1),
                () -> assertEquals(orderItems.get(0).getProduct(), "product"),
                () -> assertEquals(orderItems.get(0).getQuantity(), 2),
                () -> assertEquals(orderItems.get(0).getOrderId(), 1L)
        );
    }


    private void insertSampleData(int count) {
        for (int i = 0; i < count; i++) {
            Person person = new Person();
            person.setName("jinny_" + i);
            person.setAge(30 + i);
            person.setEmail("test@test.com");

            entityPersister.insert(person);
        }

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber("1");

        entityPersister.insert(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct("product");
        orderItem.setQuantity(2);
        orderItem.setOrderId(1L);

        entityPersister.insert(orderItem);
    }


    private void createTable() {
        ddlExcuteor.createTable(Person.class);
        ddlExcuteor.createTable(Order.class);
        ddlExcuteor.createTable(OrderItem.class);
    }

    private void dropTable() {
        ddlExcuteor.dropTable(Person.class);
        ddlExcuteor.dropTable(Order.class);
        ddlExcuteor.dropTable(OrderItem.class);
    }

}
