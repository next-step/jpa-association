package persistence.sql.dml;

import domain.Order;
import domain.OrderItem;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person("username", 50, "test@test.com", 1);
    }

    @DisplayName("Person 객체를 select one 쿼리로 변환한다.")
    @Test
    void buildFindQuery() {

        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();

        //when
        String selectOneQuery = queryBuilder.build(Person.class).toStatementWithId(1L);

        //then
        assertThat(selectOneQuery).isEqualTo("select id, nick_name, old, email from users where users.id = 1");

    }

    @DisplayName("Person 객체를 select all 쿼리로 변환한다.")
    @Test
    void testSelectAllDml() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();

        //when
        String selectAll = queryBuilder.build(Person.class).toStatement();

        //then
        assertThat(selectAll).isEqualTo("select id, nick_name, old, email from users");
    }


    @DisplayName("Order 와 OrderItem 객체를 join 쿼리로 변환한다.")
    @Test
    void testJoinQuery() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();
        String joinQuery = queryBuilder.build(Order.class).toStatement();

        //when
        //then
        assertThat(joinQuery).isEqualTo("select orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity from orders join order_items on orders.id = order_items.order_id");
    }

    @DisplayName("Order 와 OrderItem 객체를 join 쿼리로 변환한다. - where id 조건이 추가된다.")
    @Test
    void testJoinWithIdQuery() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();
        String joinQuery = queryBuilder.build(Order.class).toStatementWithId(1);

        //when
        //then
        assertThat(joinQuery).isEqualTo("select orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity from orders join order_items on orders.id = order_items.order_id where orders.id = 1");
    }

    @DisplayName("Order 와 OrderItem 객체를 join 쿼리로 변환한다. - EAGER 타입이 아니면 바로 join쿼리가 나가지 않는다.")
    @Test
    void testJoinWithIdLazyAssociationQuery() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();
        String joinQuery = queryBuilder.build(TestOrder.class).toStatementWithId(1);

        //when
        //then
        assertThat(joinQuery).isEqualTo("select id, order_number from orders where orders.id = 1");
    }

    @Table(name = "orders")
    @Entity
    public static class TestOrder {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String orderNumber;
        @OneToMany
        @JoinColumn(name = "order_id")
        private List<TestOrderItem> orderItems;
    }

    @Entity
    public static class TestOrderItem {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String product;
        private int quantity;
    }
}
