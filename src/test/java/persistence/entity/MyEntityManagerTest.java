package persistence.entity;

import database.dialect.H2Dialect;
import domain.Order;
import domain.OrderItem;
import domain.Person;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.support.DatabaseSetup;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DatabaseSetup
class MyEntityManagerTest {

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect());
        String createQuery = createQueryBuilder.build(Person.class);
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown(JdbcTemplate jdbcTemplate) {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(new H2Dialect());
        String dropQuery = dropQueryBuilder.build(Person.class);
        jdbcTemplate.execute(dropQuery);
    }

    @Test
    @DisplayName("find 메서드는 주어진 클래스와 id에 해당하는 엔티티를 반환한다")
    void find() {
        // given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Long id = 1L;
        Person expected = new Person(id, "John", 25, "qwer@asdf.com", 1);
        String insertQuery = new InsertQueryBuilder().build(expected);
        jdbcTemplate.execute(insertQuery);

        // when
        Person person = entityManager.find(Person.class, id);

        // then
        assertThat(person).isNotNull();
    }

    @Test
    @DisplayName("find 메서드는 주어진 클래스와 연관관계에 있는 엔티티까지 반환한다.")
    void find_containsOneToMany() {
        //given
        String orderCreateQuery = "CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY, orderNumber VARCHAR(255));";
        String orderItemCreateQuery = "CREATE TABLE order_items (id BIGINT AUTO_INCREMENT PRIMARY KEY, product VARCHAR(255), quantity INT, order_id BIGINT);";
        jdbcTemplate.execute(orderCreateQuery);
        jdbcTemplate.execute(orderItemCreateQuery);
        jdbcTemplate.execute("INSERT INTO order_items (id, order_id, product, quantity) VALUES (1, 1,'상품A', 1);");
        jdbcTemplate.execute("INSERT INTO order_items (id, order_id, product, quantity) VALUES (2, 1,'상품B', 2);");
        jdbcTemplate.execute("INSERT INTO orders (id, orderNumber) VALUES (1, '주문번호1');");
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);

        //when
        Order order = entityManager.find(Order.class, 1L);
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            System.out.println("orderItem = " + orderItem);
        }

        //then
        Assertions.assertThat(order.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("persist 메서드는 주어진 객체를 저장한다.")
    void persist() {
        // given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Long id = 1L;
        String expectedName = "John";
        Person expected = new Person(id, expectedName, 25, "qwer@asdf.com", 1);
        entityManager.persist(expected);

        // when
        Person person = entityManager.find(Person.class, id);

        // then
        assertThat(person).extracting("name")
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("remove 메서드는 주어진 객체를 삭제한다.")
    void remove() {
        //given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Person expected = new Person(1L, "name", 25, "qwer@asdf.com", 1);
        entityManager.persist(expected);

        //when
        entityManager.remove(expected);

        //then
        assertThatThrownBy(() -> entityManager.find(Person.class, 1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("flush 메서드는 변경된 객체를 업데이트 한다.")
    void flush() {
        //given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Person person = new Person(1L, "name", 25, "qwer@asdf.com", 1);
        entityManager.persist(person);
        String updatedName = "ABC";
        Person updated = new Person(1L, updatedName, 30, "asdf@asdf.com", 1);
        entityManager.merge(updated);

        //when & then
        entityManager.flush();
        Person result = entityManager.find(Person.class, 1L);
        assertThat(result).extracting("name").isEqualTo(updatedName);
    }
}
