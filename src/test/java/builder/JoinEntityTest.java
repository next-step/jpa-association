package builder;

import builder.dml.EntityData;
import builder.dml.JoinEntity;
import entity.Order;
import entity.OrderItem;
import jakarta.persistence.FetchType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class JoinEntityTest {

    @DisplayName("@OneToMany 관계가 포함되어있는 Class를 입력받아 JoinEntity를 생성한다.")
    @Test
    void createJoinEntityInputClassTest() {
        JoinEntity joinEntity = new JoinEntity(Order.class);
        assertThat(joinEntity.getJoinEntityData())
                .extracting("fetchType", "tableName", "joinColumnName", "alias")
                .containsExactly(tuple(FetchType.EAGER, "order_items", "order_id", "order_items_"));
    }

    @DisplayName("@OneToMany 관계가 포함되어있는 Class를 입력받아 JoinEntity를 생성한다.")
    @Test
    void createJoinEntityInputInstanceTest() {
        Order order = new Order(1L, "1234", List.of(createOrderItem(1, 1L)));

        JoinEntity joinEntity = new JoinEntity(order);
        assertThat(joinEntity.getJoinEntityData())
                .extracting("fetchType", "tableName", "joinColumnName", "alias")
                .containsExactly(tuple(FetchType.EAGER, "order_items", "order_id", "order_items_"));
    }

    private OrderItem createOrderItem(int i, Long orderId) {
        return new OrderItem((long) i, orderId, "테스트상품"+i, 1);
    }
}
