package persistence.sql.dml;

import domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.sql.meta.Table;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    @Test
    @DisplayName("select 쿼리를 만들 수 있다.")
    void buildFindQuery() {
        //given
        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.getInstance();

        //when
        String query = selectQueryBuilder.build(Table.from(Person.class), 1L);

        //then
        assertThat(query).isEqualTo("SELECT users.id, users.nick_name, users.old, users.email FROM users WHERE users.id = 1");
    }

    @Test
    @DisplayName("join select 쿼리를 만들 수 있다.")
    void buildJoinFindQuery() {
        //given
        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.getInstance();

        //when
        String query = selectQueryBuilder.buildWithJoin(Table.from(Order.class), 1L);

        //then
        assertThat(query).isEqualTo("SELECT orders.id, orders.orderNumber, order_items.id, order_items.product, order_items.quantity FROM orders LEFT JOIN order_items ON orders.id = order_items.order_id WHERE orders.id = 1");
    }
}
