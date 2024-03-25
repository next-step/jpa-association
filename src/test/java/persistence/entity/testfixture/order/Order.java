package persistence.entity.testfixture.order;

import jakarta.persistence.*;

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

    public Order(String orderNumber, List<OrderItem> orderItems) {
        this.orderNumber = orderNumber;
        this.orderItems = orderItems;
    }

    public Order(Long id, String orderNumber, List<OrderItem> orderItems) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderItems = orderItems;
    }

    public Long getId() {
        return id;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
