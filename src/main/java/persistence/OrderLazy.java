package persistence;

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

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
