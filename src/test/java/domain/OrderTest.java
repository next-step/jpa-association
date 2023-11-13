package domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import database.DatabaseServer;
import database.H2;
import domain.helper.CreateSqlHelper;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerFactory;

class OrderTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    private Order order = new Order(9999L, "1", null);

    @BeforeEach
    void init() throws SQLException {
        테이블_생성();

        OrderItem item1 = new OrderItem(30L, "빼빼로", 3);
        OrderItem item2 = new OrderItem(55L, "새우깡", 1);
        OrderItem item3 = new OrderItem(77L, "사또밥", 2);

        jdbcTemplate.execute(CreateSqlHelper.주문을_생성하는_쿼리_생성(order));
        jdbcTemplate.execute(CreateSqlHelper.주문아이템을_생성하는_쿼리_생성(item1, order));
        jdbcTemplate.execute(CreateSqlHelper.주문아이템을_생성하는_쿼리_생성(item2, order));
        jdbcTemplate.execute(CreateSqlHelper.주문아이템을_생성하는_쿼리_생성(item3, order));

        entityManager = EntityManagerFactory.of(server.getConnection());
    }

    @Test
    @DisplayName("order를 조회하여 정상적으로 생성함")
    void ii() {
        //given
        final Long orderId = 9999L;
        final String orderNumber = "1";

        //when
        Order result = entityManager.find(Order.class, 9999L);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getId()).isEqualTo(orderId);
            softAssertions.assertThat(result.getOrderNumber()).isEqualTo(orderNumber);
            softAssertions.assertThat(result.getOrderItems()).size().isEqualTo(3);
        });
    }

    private void 테이블_생성() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(CreateSqlHelper.orderCreateSql());
        jdbcTemplate.execute(CreateSqlHelper.orderItemCreateSql());
    }
}