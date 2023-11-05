package fixture;

import domain.Order;

public class OrderFixtureFactory {

    public static Order getFixture() {
        return new Order("주문1");
    }
}
