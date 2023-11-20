package util;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;

public class DataBaseTestSetUp {

    protected JdbcTemplate jdbcTemplate;
    protected Dialect dialect;
    private DatabaseServer server;
    private TestDataLoader loader;

    @BeforeEach
    void setUp() throws Exception {
        server = new H2();
        server.start();
        dialect = new FakeDialect();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        loader = new TestDataLoader(jdbcTemplate);
        loader.load("init.SQL");
    }

    @AfterEach
    void cleanUp() throws Exception {
        loader.load("cleanUp.SQL");
        server.stop();
    }
}
