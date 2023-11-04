package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Long orderId;
}
