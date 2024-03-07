package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {
    }

    private Order(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public static Order from(String orderNumber) {
        return new Order(orderNumber);
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + id +
            ", orderNumber='" + orderNumber + '\'' +
            ", orderItems=" + orderItems +
            '}';
    }
}
