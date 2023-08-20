package fixture;

import model.Order;
import model.OrderItem;

import java.util.Arrays;
import java.util.List;

public class OrderFixtures {
    public static Order createOrder() {
        return new Order(
                1L,
                "product",
                createOrderItems()
        );
    }

    public static List<OrderItem> createOrderItems() {
        return Arrays.asList(
                createOrderItem(1L),
                createOrderItem(2L),
                createOrderItem(3L)
        );
    }

    public static OrderItem createOrderItem(Long id) {
        return new OrderItem(
                id,
                "product: " + id,
                1
        );
    }
}
