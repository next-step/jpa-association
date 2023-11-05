package persistence.testFixtures.assosiate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "orders")
public class NoOneToManyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;


    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

//    @OneToMany
//    @JoinColumn(name = "order_item_id2")
//    private List<OrderItem2> orderItem2;
}
