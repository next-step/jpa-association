package persistence.entity.testentities;

import jakarta.persistence.*;

@Entity
@Table(name = "eagerload_order_items")
public class EagerLoadTestOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    public EagerLoadTestOrderItem(String product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public EagerLoadTestOrderItem(String product, Integer quantity, Long orderId) {
        this.product = product;
        this.quantity = quantity;
//        this.orderId = orderId;
    }

    public EagerLoadTestOrderItem() {
    }

    public Long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
