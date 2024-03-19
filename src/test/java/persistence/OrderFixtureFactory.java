package persistence;

import persistence.sql.Order;
import persistence.sql.OrderItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderFixtureFactory {

    public static Order generateOrderStub(final Long id, final List<OrderItem> orderItems) {
        final String orderNumber = "1";
        return new Order(id, orderNumber, orderItems);
    }

    public static Order generateOrderStub(final Long id) {
        return generateOrderStub(id, generateOrderItemsStub());
    }

    public static Order generateOrderStub() {
        return generateOrderStub(0L, generateOrderItemsStub());
    }

    public static List<OrderItem> generateOrderItemsStub(final Long... ids) {
        return Arrays.stream(ids).map(id -> new OrderItem(id, "상품 " + id, (int) (id * 1000))).collect(Collectors.toList());
    }

    public static List<OrderItem> generateOrderItemsStub() {
        return generateOrderItemsStub(1L, 2L, 3L);
    }

}
