package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    protected OrderItem() {}

    public OrderItem(String product, Integer quantity, Long orderId) {
        this.product = product;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Long orderId;

    public String getProduct() {
        return product;
    }
}
