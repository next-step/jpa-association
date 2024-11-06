package entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "orders")
public class OrderLazy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

    public OrderLazy() {
    }

    public OrderLazy(Long id) {
        this.id = id;
    }

    public OrderLazy(Long id, String orderNumber, List<OrderItem> orderItems) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderItems = orderItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
