package persistence.fixture;

import domain.Order;
import domain.OrderItem;

public class OrderFixture {

    public static Order createOrder() {
        return Order.from("order1");
    }

    public static OrderItem createOrderItem() {
        return OrderItem.of("product1", 1);
    }
}
