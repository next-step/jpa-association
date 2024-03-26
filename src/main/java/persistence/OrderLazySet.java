package persistence;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
public class OrderLazySet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Set<OrderItem> orderItems;

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }
}
