package persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class TestOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

//    // 테이블 생성때문에 필요함
//    @Column(name = "order_id", nullable = false)
//    private Long orderId;

    public TestOrderItem(String product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public TestOrderItem(String product, Integer quantity, Long orderId) {
        this.product = product;
        this.quantity = quantity;
//        this.orderId = orderId;
    }

    public TestOrderItem() {
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                '}';
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
