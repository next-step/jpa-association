package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Order;
import domain.OrderItem;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import persistence.sql.ddl.CreateDdlBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.ddl.InsertQueryBuilder;
import persistence.entity.EntityLoader;
import persistence.sql.ddl.h2.H2CreateDdlBuilder;
import persistence.sql.ddl.h2.H2DropQueryBuilder;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class DatabaseTest {
    private DatabaseServer server;
    protected JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void beforeEach() throws SQLException {
        server = new H2();
        server.start();

        CreateDdlBuilder createDdlBuilder = new H2CreateDdlBuilder();

        jdbcTemplate = new JdbcTemplate(server.getConnection());

        jdbcTemplate.execute(createDdlBuilder.createTableBuild(Person.class));
        jdbcTemplate.execute(createDdlBuilder.createTableBuild(Order.class));
        jdbcTemplate.execute(createDdlBuilder.createTableBuild(OrderItem.class));
    }

    @AfterEach
    void afterEach() {
        DropQueryBuilder dropQueryBuilder = new H2DropQueryBuilder();
        jdbcTemplate.execute(dropQueryBuilder.createQueryBuild(Person.class));

        server.stop();
    }

    public void execute(String query) {
        jdbcTemplate.execute(query);
    }

    public Object queryForObject(String query) {
        EntityLoader<Person> mapper = new EntityLoader<>(Person.class);
        return jdbcTemplate.queryForObject(query, mapper);
    }

    protected List<Person> query(String sql) {
        EntityLoader<Person> mapper = new EntityLoader<>(Person.class);
        return jdbcTemplate.query(sql, mapper);
    }

    protected void insertDb() {
        InsertQueryBuilder insertQueryBuilder = new H2InsertQueryBuilder();

        Person person = new Person("slow", 20, "email@email.com", 1);
        Order order = new Order("orderNumber1");
        OrderItem orderItem  = new OrderItem("productName", 1, 1);

        execute(insertQueryBuilder.createInsertBuild(person));
        execute(insertQueryBuilder.createInsertBuild(order));
        execute(insertQueryBuilder.createInsertBuild(orderItem));
    }
}
