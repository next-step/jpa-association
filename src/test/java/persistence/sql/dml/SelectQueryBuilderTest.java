package persistence.sql.dml;

import domain.Order;
import domain.Person;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.Table;

@DisplayName("SelectQueryBuilder class 의")
class SelectQueryBuilderTest {

    @DisplayName("generateQuery 메소드는")
    @Nested
    class GenerateQuery {

        @DisplayName("Person Entity의 select 쿼리가 만들어지는지 확인한다.")
        @Test
        void testGenerateQuery() {
            // given
            SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.getInstance();

            // when
            String query = selectQueryBuilder.generateQuery(Table.getInstance(Person.class));

            // then
            assertEquals("SELECT users.id,users.nick_name,users.old,users.email FROM users", query);
        }

        @DisplayName("Order Entity의 select 쿼리가 만들어지는지 확인한다.")
        @Test
        void testGenerateQuery2() {
            // given
            SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.getInstance();

            // when
            String query = selectQueryBuilder.generateQuery(Table.getInstance(Order.class));

            // then
            assertEquals("SELECT orders.id,orders.order_number,order_items.id,order_items.product,order_items.quantity " +
                "FROM orders LEFT JOIN order_items ON orders.id = order_items.order_id", query);
        }
    }
}
