package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    private String product;

    private Integer quantity;

    public OrderItem() {}

    public OrderItem(Long id, Long orderId, String product, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
    }
}