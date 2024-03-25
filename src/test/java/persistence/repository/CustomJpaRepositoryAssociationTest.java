package persistence.repository;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.entity.testfixture.order.Order;
import persistence.entity.testfixture.order.OrderItem;
import persistence.sql.ddl.querybuilder.CreateQueryBuilder;

import java.util.List;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE_USERS;

class CustomJpaRepositoryAssociationTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomJpaRepositoryAssociationTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private CustomJpaRepository repository;

    @BeforeAll
    static void setupOnce() {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
    @BeforeEach
    void setUp() {
        List<String> queries = List.of(
                new CreateQueryBuilder(Order.class).getQuery(),
                new CreateQueryBuilder(OrderItem.class).getQuery()
        );
        queries.forEach(x-> jdbcTemplate.execute(x));
        repository = new CustomJpaRepository(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(DROP_TABLE_USERS);
    }

    @AfterAll
    static void tearDownOnce() {
        server.stop();
    }

    @Test
    @DisplayName("JPA repository는 find 실행시 eagerFetch 관계의 엔티티 값도  가져온다.")
    void findEagerFetchedItemsTest() {
        // given
        OrderItem orderItem1 = new OrderItem("불닭볶음면", 13);
        OrderItem orderItem2 = new OrderItem("짜파게티", 9);
        Order order = new Order("김철수", List.of(orderItem1, orderItem2));
        Order expectedOrder = repository.save(order);
        List<OrderItem> expectedOrderItems = expectedOrder.getOrderItems();

        // then
        Assertions.assertThat(repository.find(Order.class, expectedOrder.getId()).get()).isEqualTo(expectedOrder);
        Assertions.assertThat(repository.find(OrderItem.class, expectedOrder.getId()).get()).isEqualTo(expectedOrderItems.get(0));
    }
}
