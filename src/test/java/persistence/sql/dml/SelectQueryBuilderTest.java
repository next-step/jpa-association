package persistence.sql.dml;

import domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.sql.column.JoinTableColumn;
import persistence.sql.dialect.Database;
import persistence.sql.dialect.Dialect;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private Person person;
    private Dialect dialect;

    @BeforeEach
    void setUp() {
        dialect = Database.MYSQL.createDialect();
        person = new Person("username", 50, "test@test.com", 1);
    }

    @DisplayName("Person 객체를 select one 쿼리로 변환한다.")
    @Test
    void buildFindQuery() {

        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder(dialect);

        //when
        String selectOneQuery = queryBuilder.build(Person.class).toStatementWithId(1L);

        //then
        assertThat(selectOneQuery).isEqualTo("select id, nick_name, old, email from users where users.id = 1");

    }

    @DisplayName("Person 객체를 select all 쿼리로 변환한다.")
    @Test
    void testSelectAllDml() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder(dialect);

        //when
        String selectAll = queryBuilder.build(Person.class).toStatement();

        //then
        assertThat(selectAll).isEqualTo("select id, nick_name, old, email from users");
    }


    @DisplayName("Order 와 OrderItem 객체를 join 쿼리로 변환한다.")
    @Test
    void testJoinQuery() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder(dialect);
        String joinQuery = queryBuilder.build(Order.class).toJoinStatement();

        //when
        //then
        assertThat(joinQuery).isEqualTo("select orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity from orders join order_items on orders.id = order_items.order_id");
    }

    @DisplayName("Order 와 OrderItem 객체를 join 쿼리로 변환한다. - where id 조건이 추가된다.")
    @Test
    void testJoinWithIdQuery() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder(dialect);
        String joinQuery = queryBuilder.build(Order.class).toJoinStatementWithId(1);

        //when
        //then
        assertThat(joinQuery).isEqualTo("select orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity from orders join order_items on orders.id = order_items.order_id where orders.id = 1");
    }

}
