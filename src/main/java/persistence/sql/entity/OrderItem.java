package persistence.sql.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    @Column(name = "order_id")
    private Long orderId;

    public OrderItem() {

    }

    public OrderItem(String product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
