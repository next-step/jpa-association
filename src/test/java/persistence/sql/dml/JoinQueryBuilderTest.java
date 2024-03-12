package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.model.JoinTable;
import persistence.sql.model.Table;
import persistence.study.sql.ddl.Order;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JoinQueryBuilderTest {

    @DisplayName("조인 쿼리 생성하기")
    @Test
    void build() throws Exception {
        Class<Order> clazz = Order.class;
        Field joinField = clazz.getDeclaredField("orderItems");

        Table table = new Table(clazz);
        JoinTable joinTable = new JoinTable(joinField);
        JoinQueryBuilder joinQueryBuilder = new JoinQueryBuilder(table, joinTable);

        String result = joinQueryBuilder.build();

        assertThat(result).isEqualTo("JOIN order_items ON orders.id=order_items.order_id");
    }
}
