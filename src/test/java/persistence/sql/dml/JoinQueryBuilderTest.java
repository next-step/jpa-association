package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.model.Table;
import persistence.study.sql.ddl.Order;

import static org.assertj.core.api.Assertions.assertThat;

class JoinQueryBuilderTest {

    @DisplayName("조인 쿼리 생성하기")
    @Test
    void build() {
        Class<Order> clazz = Order.class;

        Table table = new Table(clazz);
        JoinQueryBuilder joinQueryBuilder = new JoinQueryBuilder(table);

        String result = joinQueryBuilder.build();

        assertThat(result).isEqualTo("LEFT JOIN order_items ON orders.id=order_items.order_id");
    }
}
