package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "orders_lazy")
public class OrderLazy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<OrderLazyItem> orderItems;

    public OrderLazy(Long id, String orderNumber, List<OrderLazyItem> orderItems) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderItems = orderItems;
    }

    public OrderLazy() {

    }

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public List<OrderLazyItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public String toString() {
        return "OrderLazy{" +
            "id=" + id +
            ", orderNumber='" + orderNumber + '\'' +
            ", orderItems=" + orderItems +
            '}';
    }
}
