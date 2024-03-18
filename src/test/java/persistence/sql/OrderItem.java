package persistence.sql;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    public OrderItem(final String product, final Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public OrderItem() {

    }
}
