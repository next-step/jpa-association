package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product")
    private String product;

    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "order_id")
    private Integer orderId;

    public OrderItem(String product, int quantity, Integer orderId) {
        this.product = product;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public OrderItem() {
        
    }
}
