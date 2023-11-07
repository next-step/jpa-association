package fixture;

import domain.OrderItem;

import java.util.Arrays;
import java.util.List;

public class OrderItemFixtureFactory {

    public static List<OrderItem> getFixtures() {
        return Arrays.asList(
                new OrderItem("상품1", 1, 1L),
                new OrderItem("상품2", 2, 1L),
                new OrderItem("상품3", 3, 1L)
        );
    }
}
