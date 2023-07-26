package domain;

public final class OrderFixture {
    public static final String INSERT_ORDERS =
            "INSERT INTO orders (id, order_number) "
                    + "VALUES (1,'first'), (2,'second')";
    public static final String INSERT_ORDER_ITEMS =
            "INSERT INTO order_items (id, product, quantity, order_id) "
                    + "VALUES (1,'first_one',1,1), (2,'first_second',2,1), "
                    + "(3,'second_three',3,2), (4,'second_four',4,2), (5,'second_five',5,2)";

    private OrderFixture() {}
}
