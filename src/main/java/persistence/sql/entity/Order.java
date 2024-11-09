package persistence.sql.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

    public Order() {

    }

    public Order(String orderNumber) {
        this.orderNumber = orderNumber;
        this.orderItems = new ArrayList<>();
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
