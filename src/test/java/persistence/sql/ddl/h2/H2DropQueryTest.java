package persistence.sql.ddl.h2;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class H2DropQueryTest {
    @Test
    @DisplayName("Person Entity 를 위한 drop 쿼리를 생성한다.")
    void dropPerson() {
        final String expected = "DROP TABLE IF EXISTS users";
        assertThat(
                H2DropQuery.build(Person.class)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("Order Entity 를 위한 drop 쿼리를 생성한다.")
    void dropOrder() {
        final String expected = "DROP TABLE IF EXISTS orders";
        assertThat(
                H2DropQuery.build(Order.class)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("OrderItem Entity 를 위한 drop 쿼리를 생성한다.")
    void dropOrderItem() {
        final String expected = "DROP TABLE IF EXISTS order_items";
        assertThat(
                H2DropQuery.build(OrderItem.class)
        ).isEqualTo(expected);
    }
}
