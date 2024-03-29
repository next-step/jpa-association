package persistence.sql;

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
    private List<EagerOrderItem> eagerOrderItems;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<LazyOrderItem> lazyOrderItems;

    public Order(final Long id, final String orderNumber, final List<EagerOrderItem> eagerOrderItems, final List<LazyOrderItem> lazyOrderItems) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.eagerOrderItems = eagerOrderItems;
        this.lazyOrderItems = lazyOrderItems;
    }

    public Order() {

    }

    public Long getId() {
        return this.id;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public List<EagerOrderItem> getEagerOrderItems() {
        return this.eagerOrderItems;
    }

    public List<LazyOrderItem> getLazyOrderItems() {
        return this.lazyOrderItems;
    }
}
