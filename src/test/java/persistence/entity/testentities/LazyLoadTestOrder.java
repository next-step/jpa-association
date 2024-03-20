package persistence.entity.testentities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "lazyload_orders")
public class LazyLoadTestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<LazyLoadTestOrderItem> orderItems;

    public LazyLoadTestOrder() {
    }

    public List<LazyLoadTestOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
