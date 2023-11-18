package persistence.sql.dml.builder;

import static org.assertj.core.api.Assertions.assertThat;

import domain.Order;
import domain.OrderItem;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.MetaEntity;

public class JoinQueryBuilderTest {

  @Test
  @DisplayName("Join SQL 구문을 생성합니다.")
  public void createJoinSQL() {
    MetaEntity<Order> metaOrder = MetaEntity.of(Order.class);
    MetaEntity<OrderItem> metaOrderItem = MetaEntity.of(OrderItem.class);

    String joinQuery = new JoinQueryBuilder()
        .select(metaOrder.getTableName(),metaOrderItem.getTableName(), metaOrder.getEntityColumnsWithId(), metaOrderItem.getEntityColumnsWithId())
        .join(List.of(metaOrderItem.getTableName()))
        .on(List.of("order_id"))
        .where(metaOrder.getPrimaryKeyColumn().getDBColumnName(), List.of("1", "2"))
        .build().createJoinQuery();

    assertThat(joinQuery).isEqualTo(
        "SELECT ORDERS.id,ORDERS.ordernumber,ORDER_ITEMS.id,ORDER_ITEMS.product,ORDER_ITEMS.quantity FROM ORDERS JOIN ORDER_ITEMS ON ORDERS.id = ORDER_ITEMS.order_id WHERE ORDERS.id IN (1,2);");
  }
}
