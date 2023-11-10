package domain;

import database.DatabaseServer;
import database.H2;
import domain.helper.CreateSqlHelper;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerFactory;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    private Order order = new Order(1L, "00001", null);

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
        final Long orderId = 1L;
        final String orderNumber = "00001";

        //when
        Order result = entityManager.find(Order.class, 1L);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getId()).isEqualTo(orderId);
            softAssertions.assertThat(result.getOrderNumber()).isEqualTo(orderNumber);
        });
    }

    private void 테이블_생성() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(CreateSqlHelper.orderCreateSql());
        jdbcTemplate.execute(CreateSqlHelper.orderItemCreateSql());
    }
}