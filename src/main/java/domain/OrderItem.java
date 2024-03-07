package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;


    public OrderItem() {
    }

    private OrderItem(String product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public static OrderItem of(String product, Integer quantity) {
        return new OrderItem(product, quantity);
    }

    public Long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + id +
            ", product='" + product + '\'' +
            ", quantity=" + quantity +
            '}';
    }
}
