package persistence.meta;

import static org.assertj.core.api.Assertions.assertThat;

import domain.Order;
import domain.OrderItem;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("ForeignerColumn 테스트")
class ForeignerColumnTest {

    @Test
    @DisplayName("외래키를 생성한다.")
    void createForeignerColumn() throws Exception {
        //given
        Field pkId = OrderItem.class.getDeclaredField("id");

        //when
        ForeignerColumn foreignerColumn = ForeignerColumn.of(Order.class, pkId, "order_id");

        //then
        assertThat(foreignerColumn.getName()).isEqualTo("order_id");
    }
}
