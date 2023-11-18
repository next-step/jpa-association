package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_lazy_items")
public class OrderLazyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    public OrderLazyItem(Long id, String product, Integer quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public OrderLazyItem() { }

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
        return "OrderLazyItem{" +
            "id=" + id +
            ", product='" + product + '\'' +
            ", quantity=" + quantity +
            '}';
    }
}
