package persistence.sql.dml.h2;

import domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityMeta;

import static org.assertj.core.api.Assertions.assertThat;

class H2EagerJoinQueryTest {

    @DisplayName("Order 의 OrderItem 에 대한 fetch join 쿼리를 생성한다.")
    @Test
    void build() {
        String expected = "SELECT"
                + " t1.id, t1.order_number,"
                + " t2.id, t2.product, t2.quantity, t2.order_id"
                + " FROM orders AS t1"
                + " INNER JOIN order_items AS t2"
                + " ON t1.id = t2.order_id";
        assertThat(
                H2EagerJoinQuery.build(new EntityMeta(Order.class))
        ).isEqualTo(expected);
    }
}
