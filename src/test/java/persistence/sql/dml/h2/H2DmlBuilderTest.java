package persistence.sql.dml.h2;

import domain.Order;
import domain.Person;
import domain.PersonFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityMeta;
import persistence.sql.dml.DmlBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class H2DmlBuilderTest {
    private DmlBuilder dml;

    @BeforeEach
    void setUp() {
        dml = H2DmlBuilder.getInstance();
    }

    @Test
    @DisplayName("Person 객체를 위한 INSERT 쿼리를 생성한다.")
    void insert() {
        String expected = "INSERT INTO users"
                + " (nick_name, old, email)"
                + " VALUES ('고정완', 30, 'ghojeong@email.com')";
        assertThat(
                dml.getInsertQuery(PersonFixture.createPerson())
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("Order Entity 를 위한 join 쿼리를 생성한다.")
    void eagerJoinQuery() {
        String expected = "SELECT"
                + " t1.id AS t1_id, t1.order_number AS t1_order_number,"
                + " t2.id AS t2_id, t2.product AS t2_product, t2.quantity AS t2_quantity, t2.order_id AS t2_order_id"
                + " FROM orders AS t1"
                + " INNER JOIN order_items AS t2"
                + " ON t1.id = t2.order_id";
        assertThat(
                dml.getEagerJoinQuery(new EntityMeta(Order.class))
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("Person Entity 를 위한 findAll 쿼리를 생성한다.")
    void findAll() {
        String expected = "SELECT"
                + " id, nick_name, old, email"
                + " FROM users";
        assertThat(
                dml.getFindAllQuery(Person.class)
        ).isEqualTo(expected);
    }

    @DisplayName("Where 조건문을 생성할 수 있다.")
    @Test
    void where() {
        String expected = " WHERE id = 1, name = 'ghojeong'";
        Map<String, Object> condition = new LinkedHashMap<>();
        condition.put("id", 1);
        condition.put("name", "ghojeong");
        assertThat(dml.getWhereQuery(condition))
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Person Entity 를 위한 findById 쿼리를 생성한다.")
    void findById() {
        String expected = "SELECT"
                + " id, nick_name, old, email"
                + " FROM users"
                + " WHERE id = 1";
        assertThat(
                dml.getFindByIdQuery(Person.class, 1)
        ).isEqualTo(expected);
    }

    @Test
    @DisplayName("Person Entity 를 위한 delete 쿼리를 생성한다.")
    void delete() {
        String expected = "DELETE FROM users"
                + " WHERE id = 1";
        assertThat(
                dml.getDeleteByIdQuery(Person.class, 1)
        ).isEqualTo(expected);
    }
}
