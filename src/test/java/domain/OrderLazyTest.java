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

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

class OrderLazyTest {
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    private OrderLazy order = new OrderLazy(9999L, "1", null);

    @BeforeEach
    void init() throws SQLException {
        테이블_생성();

        OrderLazyItem item1 = new OrderLazyItem(1000L, "초코송이", 3);
        OrderLazyItem item2 = new OrderLazyItem(1155L, "포테토칩", 1);
        OrderLazyItem item3 = new OrderLazyItem(1177L, "초코땡", 2);

        jdbcTemplate.execute(CreateSqlHelper.지연주문을_생성하는_쿼리_생성(order));
        jdbcTemplate.execute(CreateSqlHelper.지연주문아이템을_생성하는_쿼리_생성(item1, order));
        jdbcTemplate.execute(CreateSqlHelper.지연주문아이템을_생성하는_쿼리_생성(item2, order));
        jdbcTemplate.execute(CreateSqlHelper.지연주문아이템을_생성하는_쿼리_생성(item3, order));

        entityManager = EntityManagerFactory.of(server.getConnection());
    }

    @Test
    @DisplayName("지연로딩으로")
    void lazyLoading() {
        //given
        final Long id = 9999L;
        OrderLazy orderLazy = entityManager.find(OrderLazy.class, id);

        //when
        OrderLazyItem result1 = orderLazy.getOrderItems().get(0);
        OrderLazyItem result2 = orderLazy.getOrderItems().get(1);
        OrderLazyItem result3 = orderLazy.getOrderItems().get(2);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderLazy.getOrderItems()).size().isEqualTo(3);
            softAssertions.assertThat(result1.getProduct()).isEqualTo("초코송이");
            softAssertions.assertThat(result2.getProduct()).isEqualTo("포테토칩");
            softAssertions.assertThat(result3.getProduct()).isEqualTo("초코땡");
        });
    }

    private void 테이블_생성() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(CreateSqlHelper.orderLazyCreateSql());
        jdbcTemplate.execute(CreateSqlHelper.orderLazyItemCreateSql());
    }
}