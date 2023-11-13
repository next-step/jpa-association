package domain;

import jakarta.persistence.*;

import java.util.Objects;

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

	public OrderItem(Long id, String product, Integer quantity) {
		this.id = id;
		this.product = product;
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, product, quantity);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}

		OrderItem orderItem = (OrderItem) obj;

		return hashCode() == orderItem.hashCode();
	}
}
