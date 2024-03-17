package testsupport;

import database.H2;
import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import database.sql.ddl.Create;
import entity.Person;
import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract public class H2DatabaseTest {
    protected static final Dialect dialect = MySQLDialect.getInstance();

    protected static H2 server;
    protected Connection connection;
    protected JdbcTemplate jdbcTemplate;
    protected List<String> executedQueries;

    @BeforeAll
    static void startServer() throws SQLException {
        server = new H2();
        server.start();
    }

    @BeforeEach
    void initJdbcTemplate() throws SQLException {
        connection = server.getConnection();
        createTable();

        executedQueries = new ArrayList<>();
        jdbcTemplate = createJdbcTemplateProxy(executedQueries);
    }

    private JdbcTemplate createJdbcTemplateProxy(List<String> executedQueries) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(JdbcTemplate.class);
        enhancer.setCallback(new LoggingJdbcMethodInterceptor(executedQueries));
        return (JdbcTemplate) enhancer.create(new Class<?>[]{Connection.class}, new Object[]{connection});
    }

    private void createTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        jdbcTemplate.execute("DROP TABLE users IF EXISTS");
        jdbcTemplate.execute(new Create(Person.class, dialect).buildQuery());
    }

    @AfterAll
    static void shutdownServer() {
        server.stop();
    }
}
