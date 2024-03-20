package persistence.entity.testentities;

import jakarta.persistence.*;

@Entity
@Table(name = "lazyload_order_items")
public class LazyLoadTestOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

//    // 테이블 생성때문에 필요함
//    @Column(name = "order_id", nullable = false)
//    private Long orderId;

    public LazyLoadTestOrderItem(String product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public LazyLoadTestOrderItem(String product, Integer quantity, Long orderId) {
        this.product = product;
        this.quantity = quantity;
//        this.orderId = orderId;
    }

    public LazyLoadTestOrderItem() {
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
