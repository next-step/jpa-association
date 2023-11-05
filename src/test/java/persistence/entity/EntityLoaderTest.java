package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Order;
import domain.OrderItem;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DdlQueryGenerator;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.meta.EntityMeta;
import persistence.sql.meta.MetaFactory;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private DdlQueryGenerator ddlQueryGenerator;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        DialectFactory dialectFactory = DialectFactory.getInstance();
        ddlQueryGenerator = DdlQueryGenerator.of(dialectFactory.getDialect(jdbcTemplate.getDbmsName()));

        EntityMeta personMeta = MetaFactory.get(Person.class);
        EntityMeta orderMeta = MetaFactory.get(Order.class);
        EntityMeta orderItemMeta = MetaFactory.get(OrderItem.class);
        jdbcTemplate.execute(ddlQueryGenerator.generateCreateQuery(personMeta));
        jdbcTemplate.execute(ddlQueryGenerator.generateCreateQuery(orderMeta));
        jdbcTemplate.execute(ddlQueryGenerator.generateCreateQuery(orderItemMeta));
    }

    @AfterEach
    void tearDown() {
        EntityMeta personMeta = MetaFactory.get(Person.class);
        EntityMeta orderMeta = MetaFactory.get(Order.class);
        EntityMeta orderItemMeta = MetaFactory.get(OrderItem.class);
        jdbcTemplate.execute(ddlQueryGenerator.generateDropQuery(personMeta));
        jdbcTemplate.execute(ddlQueryGenerator.generateDropQuery(orderMeta));
        jdbcTemplate.execute(ddlQueryGenerator.generateDropQuery(orderItemMeta));
        server.stop();
    }

    @Test
    @DisplayName("엔티티 조회 - 저장된 식별자로 엔티티를 조회하여 인스턴스의 존재를 확인한다")
    void find() {
        EntityLoader entityLoader = EntityLoader.of(jdbcTemplate);

        Person person = entityLoader.selectOne(Person.class, 1L);
        assertThat(person).isNull();
    }

}
