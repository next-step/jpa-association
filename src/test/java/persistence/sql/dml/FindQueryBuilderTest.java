package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.entity.EntityBinder;
import persistence.entity.EntityId;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;
import persistence.study.sql.ddl.Order;
import persistence.study.sql.ddl.Person1;
import persistence.study.sql.ddl.Person2;
import persistence.study.sql.ddl.Person3;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FindQueryBuilderTest {

    @DisplayName("Person을 이용하여 findAll 쿼리 생성하기")
    @ParameterizedTest
    @MethodSource
    void build(Table table, String findAllQuery) {
        FindQueryBuilder findQueryBuilder = new FindQueryBuilder(table);

        String result = findQueryBuilder.build();

        assertThat(result).isEqualTo(findAllQuery);
    }

    private static Stream<Arguments> build() {
        return Stream.of(
                Arguments.arguments(new Table(Person1.class), "SELECT person1.id,person1.name,person1.age FROM person1"),
                Arguments.arguments(new Table(Person2.class), "SELECT person2.id,person2.nick_name,person2.old,person2.email FROM person2"),
                Arguments.arguments(new Table(Person3.class), "SELECT users.id,users.nick_name,users.old,users.email FROM users")
        );
    }

    @DisplayName("Person을 이용하여 findById 쿼리 생성하기")
    @ParameterizedTest
    @MethodSource
    void buildById(Table table, Object person, String findByIdQuery) {
        PKColumn pkColumn = table.getPKColumn();
        EntityBinder entityBinder = new EntityBinder(person);

        Object idValue = entityBinder.getValue(pkColumn);
        EntityId id = new EntityId(idValue);
        FindQueryBuilder findQueryBuilder = new FindQueryBuilder(table);

        String result = findQueryBuilder.buildById(id);

        assertThat(result).isEqualTo(findByIdQuery);
    }

    private static Stream<Arguments> buildById() {
        return Stream.of(
                Arguments.arguments(new Table(Person1.class), new Person1(1L, "qwer", 1), "SELECT person1.id,person1.name,person1.age FROM person1 WHERE person1.id=1"),
                Arguments.arguments(new Table(Person2.class), new Person2(2L, "qwert", 2, "email@email.com"), "SELECT person2.id,person2.nick_name,person2.old,person2.email FROM person2 WHERE person2.id=2"),
                Arguments.arguments(new Table(Person3.class), new Person3(3L, "qwerty", 3, "email2@email.com"), "SELECT users.id,users.nick_name,users.old,users.email FROM users WHERE users.id=3")
        );
    }

    @DisplayName("Order를 이용하여 find 조인 쿼리 확인하기")
    @Test
    void buildWithJoin() {
        Table table = new Table(Order.class);

        FindQueryBuilder findQueryBuilder = new FindQueryBuilder(table);

        String result = findQueryBuilder.build();

        assertThat(result).isEqualTo("SELECT orders.id,orders.order_number,order_items.id,order_items.product,order_items.quantity FROM orders LEFT JOIN order_items ON orders.id=order_items.order_id");
    }

    @DisplayName("Order를 이용하여 findById 조인 쿼리 확인하기")
    @Test
    void buildByIdWithJoin() {
        Table table = new Table(Order.class);

        FindQueryBuilder findQueryBuilder = new FindQueryBuilder(table);
        EntityId id = new EntityId(1L);

        String result = findQueryBuilder.buildById(id);

        assertThat(result).isEqualTo("SELECT orders.id,orders.order_number,order_items.id,order_items.product,order_items.quantity FROM orders LEFT JOIN order_items ON orders.id=order_items.order_id WHERE orders.id=1");
    }
}
