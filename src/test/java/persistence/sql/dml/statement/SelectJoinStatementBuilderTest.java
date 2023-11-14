package persistence.sql.dml.statement;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.OnPredicate;
import persistence.sql.dml.clause.predicate.WherePredicate;

@DisplayName("SELECT 문 생성 테스트")
class SelectJoinStatementBuilderTest {

    @Test
    @DisplayName("Join절을 통해 Select 문을 생성할 수 있다.")
    void canBuildSelectStatementJoin() {
        final String selectStatement = SelectStatementBuilder.builder()
            .selectFrom(SelectJoinStatementEntity.class, new H2ColumnType())
            .leftJoin(OrderItem.class, OnPredicate.of("orders.id", "order_items.order_id", new EqualOperator()), new H2ColumnType())
            .where(WherePredicate.of("id", 1L, new EqualOperator()))
            .or(WherePredicate.of("nick_name", "test_person", new EqualOperator()))
            .build();

        assertThat(selectStatement).isEqualTo("SELECT * FROM ORDERS "
            + "JOIN ORDER_ITEMS ON orders.id = order_items.order_id"
            + " WHERE id = 1 OR nick_name = 'test_person';");
    }

    @Entity
    @Table(name = "orders")
    public static class SelectJoinStatementEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "nick_name")
        private String name;

        @Column(name = "old")
        private Integer age;

        @Column(nullable = false)
        private String email;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "order_id")
        private List<OrderItem> orderItems = new ArrayList<>();

        public SelectJoinStatementEntity(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        protected SelectJoinStatementEntity() {

        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public String getEmail() {
            return email;
        }
    }

    @Entity
    @Table(name = "order_items")
    private static class OrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;
    }
}
