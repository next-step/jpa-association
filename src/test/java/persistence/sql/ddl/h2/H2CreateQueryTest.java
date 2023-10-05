package persistence.sql.ddl.h2;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class H2CreateQueryTest {

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
                H2CreateQuery.build(Person.class)
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
                H2CreateQuery.build(Order.class)
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
                H2CreateQuery.build(OrderItem.class)
        ).isEqualTo(expected);
    }
}
