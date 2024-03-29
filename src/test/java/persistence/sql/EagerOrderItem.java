package persistence.sql;

import jakarta.persistence.*;

@Entity
@Table(name = "eager_order_items")
public class EagerOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;

    public EagerOrderItem(final Long id, final String product, final Integer quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public EagerOrderItem() {

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
