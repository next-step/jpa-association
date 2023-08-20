package persistence.entity.model;

import model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OneToManyColumnTest {

    @Test
    @DisplayName("외래키를 반환한다")
    void getForeignKeyName() throws NoSuchFieldException {
        // given
        Class<Order> orderClass = Order.class;
        OneToManyColumn oneToManyColumn = OneToManyColumn.of(orderClass.getDeclaredField("orderItems"));

        // when
        String foreignKeyName = oneToManyColumn.getForeignKeyName();

        // then
        assertThat(foreignKeyName).isEqualTo("order_id");
    }
}