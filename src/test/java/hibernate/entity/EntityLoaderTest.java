package hibernate.entity;

import database.DatabaseServer;
import database.H2;
import hibernate.ddl.CreateQueryBuilder;
import hibernate.entity.meta.EntityClass;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityLoaderTest {


    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private final EntityLoader entityLoader = new EntityLoader(jdbcTemplate);

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(CreateQueryBuilder.INSTANCE.generateQuery(EntityClass.getInstance(EntityManagerImplTest.TestEntity.class)));
        jdbcTemplate.execute("CREATE TABLE orders (\n" +
                "    id BIGINT PRIMARY KEY,\n" +
                "    orderNumber VARCHAR\n" +
                ");\n");
        jdbcTemplate.execute("CREATE TABLE eager_order_items (\n" +
                "    id BIGINT PRIMARY KEY,\n" +
                "    eager_order_id BIGINT,\n" +
                "    product VARCHAR,\n" +
                "    quantity INTEGER\n" +
                ");\n");
        jdbcTemplate.execute("CREATE TABLE lazy_order_items (\n" +
                "    id BIGINT PRIMARY KEY,\n" +
                "    lazy_order_id BIGINT,\n" +
                "    product VARCHAR,\n" +
                "    quantity INTEGER\n" +
                ");\n");
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("truncate table test_entity;");
        jdbcTemplate.execute("truncate table orders;");
        jdbcTemplate.execute("truncate table eager_order_items;");
        jdbcTemplate.execute("truncate table lazy_order_items;");
    }

    @AfterAll
    static void afterAll() {
        jdbcTemplate.execute("drop table test_entity;");
        jdbcTemplate.execute("drop table orders;");
        jdbcTemplate.execute("drop table eager_order_items;");
        jdbcTemplate.execute("drop table lazy_order_items;");
        server.stop();
    }

    @Test
    void find_쿼리를_실행한다() {
        // given
        jdbcTemplate.execute("insert into test_entity (id, nick_name, age) values (1, '최진영', 19)");

        // when
        TestEntity actual = entityLoader.find(EntityClass.getInstance(TestEntity.class), 1L);

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("최진영"),
                () -> assertThat(actual.age).isEqualTo(19)
        );
    }

    @Test
    void eager_lazy_join_모두_포함된_entity를_반환한다() {
        // given
        jdbcTemplate.execute("insert into orders (id, orderNumber) values (1, 'ABC123');");
        jdbcTemplate.execute("insert into eager_order_items (id, eager_order_id, product, quantity) values (1, 1, '라면', 3);");
        jdbcTemplate.execute("insert into eager_order_items (id, eager_order_id, product, quantity) values (2, 1, '김치', 2);");
        jdbcTemplate.execute("insert into lazy_order_items (id, lazy_order_id, product, quantity) values (1, 1, '라면', 3);");
        jdbcTemplate.execute("insert into lazy_order_items (id, lazy_order_id, product, quantity) values (2, 1, '김치', 2);");
        jdbcTemplate.execute("insert into lazy_order_items (id, lazy_order_id, product, quantity) values (3, 1, '바나나', 4);");

        // when
        Order actual = entityLoader.find(EntityClass.getInstance(Order.class), 1L);

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.eagerOrderItems).hasSize(2),
                () -> assertThat(actual.lazyOrderItems).hasSize(3)
        );
    }

    @Test
    void findAll_쿼리를_실행한다() {
        // given
        jdbcTemplate.execute("insert into test_entity (id, nick_name, age) values (1, '최진영', 19)");
        jdbcTemplate.execute("insert into test_entity (id, nick_name, age) values (2, '진영최', 29)");

        // when
        List<TestEntity> actual = entityLoader.findAll(EntityClass.getInstance(TestEntity.class));

        // then
        assertThat(actual).hasSize(2);
    }

    @Entity
    @Table(name = "test_entity")
    static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "nick_name")
        private String name;

        private Integer age;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public TestEntity(String name) {
            this.name = name;
        }
    }

    @Entity
    @Table(name = "orders")
    static class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String orderNumber;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "eager_order_id")
        private List<EagerOrderItem> eagerOrderItems;

        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(name = "lazy_order_id")
        private List<LazyOrderItem> lazyOrderItems;
    }


    @Entity
    @Table(name = "eager_order_items")
    static class EagerOrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;
    }

    @Entity
    @Table(name = "lazy_order_items")
    static class LazyOrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;
    }
}
