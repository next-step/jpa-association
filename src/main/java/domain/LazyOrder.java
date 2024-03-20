package domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lazy_orders")
public class LazyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

    public LazyOrder() {
    }

    public LazyOrder(final Long id,
                     final String orderNumber,
                     final List<OrderItem> orderItems) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderItems = orderItems;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LazyOrder lazyOrder = (LazyOrder) o;
        return Objects.equals(id, lazyOrder.id) && Objects.equals(orderNumber, lazyOrder.orderNumber) && Objects.equals(orderItems, lazyOrder.orderItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber, orderItems);
    }
}
