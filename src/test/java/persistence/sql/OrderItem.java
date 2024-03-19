package persistence.sql;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    public OrderItem(final Long id, final String product, final Integer quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public OrderItem() {

    }

    public String getProduct() {
        return this.product;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

}
