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

import static org.assertj.core.api.Assertions.assertThat;

class CustomSelectQueryBuilderTest extends JpaTest {

    static EntityMetaData entityMetaData;

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @BeforeEach
    void setUp() {
        entityMetaData = new EntityMetaData(Order.class);
        initForTest(entityMetaData);
        createOrderAndOrderItemTable();
        insertOrderAndOrderItemData();
    }

    @AfterEach
    void remove() {
        dropOrderAndOrderItemTable();
    }

    @AfterAll
    static void destroy() {
        server.stop();
    }

    @DisplayName("OneToMany 를 갖고 있는 Entity 클래스의 select 쿼리는 join 문 포함 필요")
    @Test
    void selectSqlWithJoinColumn() {
        entityMetaData = new EntityMetaData(Order.class);

        CustomSelectQueryBuilder customSelectQueryBuilder = new CustomSelectQueryBuilder(entityMetaData);
        String selectJoinQuery = customSelectQueryBuilder.findByIdJoinQuery(order, Order.class);

        String resultQuery = "SELECT orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity FROM orders LEFT JOIN order_items ON orders.id = order_items.order_id WHERE orders.id = 1;";
        assertThat(selectJoinQuery).isEqualTo(resultQuery);
    }
}
