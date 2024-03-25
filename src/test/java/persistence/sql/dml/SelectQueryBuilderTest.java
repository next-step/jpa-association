package persistence.sql.dml;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.entity.testfixture.order.Order;
import persistence.sql.common.DtoMapper;
import persistence.sql.ddl.querybuilder.CreateQueryBuilder;
import persistence.sql.dml.querybuilder.InsertQueryBuilder;
import persistence.sql.dml.querybuilder.SelectQueryBuilder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static persistence.sql.ddl.common.TestSqlConstant.DROP_TABLE;

class SelectQueryBuilderTest {
    private static final Logger logger = LoggerFactory.getLogger(SelectQueryBuilderTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setUpOnce() {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }

        String query = new CreateQueryBuilder(Person.class).getQuery();
        jdbcTemplate.execute(query);
    }
    @AfterAll
    static void tearDownOnce() {
        jdbcTemplate.execute(DROP_TABLE);
        server.stop();
    }

    @DisplayName("[요구사항2] 3건의 person insert 후, findAll을 실행시, 3건이 조회된다.")
    @Test
    void 요구사항2_test() {
        // given
        List<String> insertQueries = Stream.of(
                new Person("김철수", 21, "chulsoo.kim@gmail.com", 11),
                        new Person("김영희", 15, "younghee.kim@gmail.com", 11),
                        new Person("신짱구", 15, "jjangoo.sin@gmail.com", 11))
                .map(person -> new InsertQueryBuilder(Person.class).getInsertQuery(person)).collect(Collectors.toList());

        for (String query : insertQueries) {
            jdbcTemplate.execute(query);
        }

        // when
        String findAllQuery = new SelectQueryBuilder(Person.class).getFindAllQuery();
        List<Person> persons = jdbcTemplate.query(findAllQuery, new DtoMapper<>(Person.class));

        System.out.println(persons.size());
        // then
        Assertions.assertThat(persons).hasSize(3);
    }

    @DisplayName("selectAll 쿼리 생성시, entity내 eager Load 가 있으면 Join 구문을 생성한다.")
    @Test
    void join_구문_test() {
        String query = new SelectQueryBuilder(Order.class).getFindAllQuery();

        // then
        Assertions.assertThat(query).isEqualTo("SELECT * FROM orders inner join order_items on orders.id = order_items.order_id");
    }
}
