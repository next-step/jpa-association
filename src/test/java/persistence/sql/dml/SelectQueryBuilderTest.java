package persistence.sql.dml;

import domain.Order;
import jakarta.persistence.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.sql.column.TableColumn;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    @DisplayName("Person 객체를 select one 쿼리로 변환한다.")
    @Test
    void buildFindQuery() {

        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();

        //when
        String selectOneQuery = queryBuilder.build(Person.class).selectFromWhereIdClause(1L);

        //then
        assertThat(selectOneQuery).isEqualTo("select id, nick_name, old, email from users where users.id = 1");

    }

    @DisplayName("Person 객체를 select all 쿼리로 변환한다.")
    @Test
    void testSelectAllDml() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();

        //when
        String selectAll = queryBuilder.build(Person.class).selectFromClause();

        //then
        assertThat(selectAll).isEqualTo("select id, nick_name, old, email from users");
    }


    @DisplayName("Order 와 OrderItem 객체를 join 쿼리로 변환한다.")
    @Test
    void testJoinQuery() {
        //given
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();
        TableColumn tableColumn = new TableColumn(Order.class);
        String joinQuery = queryBuilder.build(Order.class).selectFromJoinClause(tableColumn.getJoinTableColumns());

        //when
        //then
        assertThat(joinQuery).isEqualTo("select orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity from orders join order_items on orders.id = order_items.order_id");
    }

    @DisplayName("Order 와 OrderItem 객체를 join 쿼리로 변환한다. - where id 조건이 추가된다.")
    @Test
    void testJoinWithIdQuery() {
        //given
        TableColumn tableColumn = new TableColumn(Order.class);

        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();
        String joinQuery = queryBuilder.build(Order.class).selectFromJoinWhereIdClause(tableColumn.getJoinTableColumns(), 1L);

        //when
        //then
        assertThat(joinQuery).isEqualTo("select orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity from orders join order_items on orders.id = order_items.order_id where orders.id = 1");
    }

    @DisplayName("연관관계가 2개 있으면 연관된 모든 엔티티에 join이 걸려서 나간다. - where id 조건이 추가된다.")
    @Test
    void manyAssociationJoinQuery() {
        //given
        TableColumn tableColumn = new TableColumn(TestOrder.class);
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder();
        String query = queryBuilder.build(TestOrder.class).selectFromJoinWhereIdClause(tableColumn.getJoinTableColumns(), 1L);
        //when
        //then
        assertThat(query).isEqualTo("select orders.id, orders.order_number, test_order_item.id, test_order_item.product, test_order_item.quantity, test_person.id, test_person.nick_name, test_person.old, test_person.email from orders join test_order_item on orders.id = test_order_item.order_id join test_person on orders.id = test_person.test_person_id where orders.id = 1");
    }

    @Table(name = "orders")
    @Entity
    public static class TestOrder {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String orderNumber;
        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "order_id")
        private List<TestOrderItem> orderItems;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "test_person_id")
        private List<TestPerson> testPersons;
    }

    @Entity
    public static class TestOrderItem {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String product;
        private int quantity;
    }

    @Entity
    public static class TestPerson {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String nickName;
        private int old;
        private String email;

        public TestPerson(String nickName, int old, String email, int id) {
            this.nickName = nickName;
            this.old = old;
            this.email = email;
            this.id = (long) id;
        }

        public TestPerson() {

        }
    }
}
