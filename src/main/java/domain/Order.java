package domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderNumber;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id")
	private List<OrderItem> orderItems;

	public Order() {
	}

	public Order(Long id, String orderNumber, List<OrderItem> orderItems) {
		this.id = id;
		this.orderNumber = orderNumber;
		this.orderItems = orderItems;
	}

	public Order(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, orderNumber, orderItems);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}

		if (!(obj instanceof Order order)) {
			return false;
		}

		return hashCode() == order.hashCode();
	}
}

