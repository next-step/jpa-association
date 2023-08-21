package persistence.entity;

import domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;
import persistence.sql.ddl.h2.H2UpdateQueryBuilder;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
class EntityProxyTest extends DatabaseTest {
    private QueryBuilder queryBuilder;


    @BeforeEach
    public void beforeEach() throws SQLException {
        super.beforeEach();
        queryBuilder = new QueryBuilder(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), new H2InsertQueryBuilder(), new H2UpdateQueryBuilder(), jdbcTemplate);
    }

    @Test
    void join_proxy() {
        insertDb();
        Order order = new Order(1L, "order11");
        Order proxy = (Order) EntityProxy.createProxy(order, queryBuilder);

        assertEquals(proxy.orderItemCount(), 2);
    }
}
