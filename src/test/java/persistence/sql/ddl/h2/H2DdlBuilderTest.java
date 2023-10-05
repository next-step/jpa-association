package persistence.sql.ddl.h2;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DdlBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class H2DdlBuilderTest {
    private DdlBuilder ddl;

    @BeforeEach
    void setUp() {
        ddl = H2DdlBuilder.getInstance();
    }

    @Test
    @DisplayName("Person Entity 를 위한 CREATE 쿼리를 생성한다.")
    void createPerson() {
        String expected = "CREATE TABLE users ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "nick_name VARCHAR(255), "
                + "old INTEGER, "
                + "email VARCHAR(320) NOT NULL"
                + ")";
        assertThat(
                ddl.getCreateQuery(Person.class)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("Order Entity 를 위한 CREATE 쿼리를 생성한다.")
    void createOrder() {
        String expected = "CREATE TABLE orders ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "order_number VARCHAR(255)"
                + ")";
        assertThat(
                ddl.getCreateQuery(Order.class)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("OrderItem Entity 를 위한 CREATE 쿼리를 생성한다.")
    void createOrderItem() {
        String expected = "CREATE TABLE order_items ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "product VARCHAR(255), "
                + "quantity INTEGER, "
                + "order_id BIGINT"
                + ")";
        assertThat(
                ddl.getCreateQuery(OrderItem.class)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("Person Entity 를 위한 drop 쿼리를 생성한다.")
    void dropPerson() {
        final String expected = "DROP TABLE IF EXISTS users";
        assertThat(
                ddl.getDropQuery(Person.class)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("Order Entity 를 위한 drop 쿼리를 생성한다.")
    void dropOrder() {
        final String expected = "DROP TABLE IF EXISTS orders";
        assertThat(
                ddl.getDropQuery(Order.class)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("OrderItem Entity 를 위한 drop 쿼리를 생성한다.")
    void dropOrderItem() {
        final String expected = "DROP TABLE IF EXISTS order_items";
        assertThat(
                ddl.getDropQuery(OrderItem.class)
        ).isEqualTo(expected);
    }
}
