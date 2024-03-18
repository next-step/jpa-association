package domain;

import jakarta.persistence.*;

import java.util.List;

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
}
