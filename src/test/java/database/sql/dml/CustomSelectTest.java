package database.sql.dml;

import entity.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CustomSelectTest {
    private final CustomSelect customSelect = new CustomSelect(Order.class);

    @Test
    void buildSelectQueryWithoutCondition() {
        String actual = customSelect.buildQuery();
        assertThat(actual).isEqualTo("SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM orders t LEFT JOIN order_items a0 ON t.id = a0.order_id");
    }

    @Test
    void buildSelectQueryWithId() {
        String actual = customSelect.buildQuery(Map.of("id", "123"));
        assertThat(actual).isEqualTo("SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM orders t LEFT JOIN order_items a0 ON t.id = a0.order_id WHERE t.id = 123");
    }

    @Test
    void buildSelectQueryWithCondition() {
        String actual = customSelect.buildQuery(Map.of("orderNumber", "order-1"));
        assertThat(actual).isEqualTo("SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM orders t LEFT JOIN order_items a0 ON t.id = a0.order_id WHERE t.orderNumber = 'order-1'");
    }
}
