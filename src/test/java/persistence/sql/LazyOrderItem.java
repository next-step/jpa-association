package persistence.sql;

import jakarta.persistence.*;

@Entity
@Table(name = "lazy_order_items")
public class LazyOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    public LazyOrderItem(final Long id, final String product, final Integer quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public LazyOrderItem() {

    }

    public Long getId() {
        return this.id;
    }

    public String getProduct() {
        return this.product;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

}
