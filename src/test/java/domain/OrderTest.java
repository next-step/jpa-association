package domain;

import database.DatabaseServer;
import database.H2;
import domain.helper.CreateSqlHelper;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerFactory;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void init() throws SQLException {
        테이블_생성();

        Order order = new Order(1L, "00001", null);
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
    @DisplayName("오류 없이 join select가 되는지 확인")
    void ii() {
        //when & then
        assertDoesNotThrow(() -> entityManager.find(Order.class, 1L));
    }

    private void 테이블_생성() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(CreateSqlHelper.orderCreateSql());
        jdbcTemplate.execute(CreateSqlHelper.orderItemCreateSql());
    }
}