package persistence.entity.testentities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "eagerload_orders")
public class EagerLoadTestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<EagerLoadTestOrderItem> orderItems;

    public EagerLoadTestOrder() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public EagerLoadTestOrder(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public List<EagerLoadTestOrderItem> getOrderItems() {
        return orderItems;
    }
}
