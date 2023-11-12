package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;


class SelectQueryBuilderTest {
    private Dialect dialect;

    @BeforeEach
    void setUp() {
        dialect = new FakeDialect();
    }

    @Test
    @DisplayName("전체를 조회하는 구문을 생성한다.")
    void selectAll() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Person.class, dialect).select();

        //when
        String sql = select.findAllQuery();

        //then
        assertThat(sql).isEqualTo("SELECT users_0.id as users_0_id"
                + ", users_0.nick_name as users_0_nick_name"
                + ", users_0.old as users_0_old"
                + ", users_0.email as users_0_email"
                + " FROM users users_0");
    }

    @Test
    @DisplayName("아이디를 기준으로 조회하는 구문을 생성한다.")
    void findById() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Person.class, dialect).select();

        //when
        String sql = select.findByIdQuery(1L);

        //then
        assertThat(sql).isEqualTo("SELECT users_0.id as users_0_id"
                + ", users_0.nick_name as users_0_nick_name"
                + ", users_0.old as users_0_old"
                + ", users_0.email as users_0_email"
                + " FROM users users_0"
                + " WHERE users_0.id = 1");
    }

    @Test
    @DisplayName("아이디가 없으면 예외가 발생한다.")
    void findByIdException() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Person.class, dialect).select();

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> select.findByIdQuery(null));
    }

    @Test
    @DisplayName("조인을 이용한 조회")
    void join() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Order.class, dialect).select();

        //when
        String sql = select.findAllQuery();

        //then
        assertThat(sql).isEqualTo("SELECT "
                + "orders_0.id as orders_0_id"
                + ", orders_0.orderNumber as orders_0_orderNumber"
                + ", order_items_1.order_id as order_items_1_order_id"
                + ", order_items_1.id as order_items_1_id"
                + ", order_items_1.product as order_items_1_product"
                + ", order_items_1.quantity as order_items_1_quantity"
                + " FROM orders orders_0"
                + " LEFT JOIN order_items order_items_1"
                + " ON orders_0.id = order_items_1.order_id");
    }

    @Test
    @DisplayName("아이디를 기준으로 조인을 이용한 조회")
    void joinById() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Order.class, dialect).select();

        //when
        String sql = select.findByIdQuery(1L);

        //then
        assertThat(sql).isEqualTo("SELECT "
                + "orders_0.id as orders_0_id"
                + ", orders_0.orderNumber as orders_0_orderNumber"
                + ", order_items_1.order_id as order_items_1_order_id"
                + ", order_items_1.id as order_items_1_id"
                + ", order_items_1.product as order_items_1_product"
                + ", order_items_1.quantity as order_items_1_quantity"
                + " FROM orders orders_0"
                + " LEFT JOIN order_items order_items_1"
                + " ON orders_0.id = order_items_1.order_id"
                + " WHERE orders_0.id = 1"
        );
    }

    @Test
    @DisplayName("외래키를 기준으로 조회")
    void findByForeignerId() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Order.class, dialect).select();

        //when
        String sql = select.findByForeignerId(1L);

        //then
        assertThat(sql).isEqualTo("SELECT order_items_0.order_id as order_items_0_order_id,"
                + " order_items_0.id as order_items_0_id,"
                + " order_items_0.product as order_items_0_product,"
                + " order_items_0.quantity as order_items_0_quantity "
                + "FROM order_items order_items_0 WHERE order_items_0.order_id = 1");
    }

    @Test
    @DisplayName("외래키의 ID가 없으면 예외가 발생한다.")
    void findByForeignerIdException() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Order.class, dialect).select();

        //when & then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> select.findByForeignerId(null));

    }
}
