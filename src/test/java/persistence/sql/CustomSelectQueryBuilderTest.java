package persistence.sql;

import database.H2;
import entity.Order;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.JpaTest;
import persistence.sql.dml.CustomSelectQueryBuilder;
import pojo.EntityMetaData;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomSelectQueryBuilderTest extends JpaTest {

    static EntityMetaData entityMetaData;

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        entityMetaData = new EntityMetaData(Order.class, order);
        initForTest(entityMetaData);
    }

    @BeforeEach
    void setUp() {
        createTable();
        insertData();
    }

    @AfterEach
    void remove() {
        dropTable();
    }

    @AfterAll
    static void destroy() {
        server.stop();
    }

    @DisplayName("OneToMany 를 갖고 있는 Entity 클래스의 select 쿼리는 join 문 포함 필요")
    @Test
    void selectSqlWithJoinColumn() {
        EntityMetaData entityMetaData = new EntityMetaData(Order.class, order);

        CustomSelectQueryBuilder customSelectQueryBuilder = new CustomSelectQueryBuilder(entityMetaData);
        String selectJoinQuery = customSelectQueryBuilder.findByIdJoinQuery(order, Order.class);

        String resultQuery = "SELECT orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity FROM orders LEFT JOIN order_items ON orders.id = order_items.order_id WHERE orders.id = 1;";
        assertThat(selectJoinQuery).isEqualTo(resultQuery);
    }

    @DisplayName("findById 테스트 - 연관관계가 있는 경우")
    @Test
    void findByIdWithAssociationTest() {
        List<? extends Order> savedOrderList = entityLoader.findByIdWithAssociation(order.getClass(), order, order.getId());
        assertThat(savedOrderList).hasSize(3);
    }

    private void createTable() {
        String createOrderSql = "create table orders(id bigint auto_increment primary key, order_number varchar(255) null);";
        String createOrderItemSql = "create table order_items(id bigint auto_increment primary key, product  varchar(255) null, quantity int null, order_id bigint null, foreign key (order_id) references orders (id));";

        jdbcTemplate.execute(createOrderSql);
        jdbcTemplate.execute(createOrderItemSql);
    }

    private void insertData() {
        String insertOrderSql = "insert into orders (id, order_number) values (" + order.getId() + ", '" + order.getOrderNumber() + "');";
        String insertOrderItemSql1 = "insert into order_items (id, product, quantity, order_id) " +
                "values (" + orderItem1.getId() + ", '" + orderItem1.getProduct() + "' , " + orderItem1.getQuantity() + ", " + order.getId() + ");";
        String insertOrderItemSql2 = "insert into order_items (id, product, quantity, order_id) " +
                "values (" + orderItem2.getId() + ", '" + orderItem2.getProduct() + "' , " + orderItem2.getQuantity() + ", " + order.getId() + ");";
        String insertOrderItemSql3 = "insert into order_items (id, product, quantity, order_id) " +
                "values (" + orderItem3.getId() + ", '" + orderItem3.getProduct() + "' , " + orderItem3.getQuantity() + ", " + order.getId() + ");";

        jdbcTemplate.execute(insertOrderSql);
        jdbcTemplate.execute(insertOrderItemSql1);
        jdbcTemplate.execute(insertOrderItemSql2);
        jdbcTemplate.execute(insertOrderItemSql3);
    }

    private void dropTable() {
        String dropOrderTable = "drop table orders;";
        String dropOrderItemTable = "drop table order_items;";

        jdbcTemplate.execute(dropOrderItemTable);
        jdbcTemplate.execute(dropOrderTable);
    }
}
