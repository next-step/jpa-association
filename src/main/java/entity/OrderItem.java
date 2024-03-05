package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    public OrderItem(String product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public OrderItem() {
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
