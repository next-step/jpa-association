package persistence.entity.database;

import database.dialect.MySQLDialect;
import database.mapping.SingleRowMapperFactory;
import database.sql.dml.Select;
import database.sql.dml.SelectByPrimaryKey;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final MySQLDialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, MySQLDialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> List<T> load(Class<T> clazz, Collection<Long> ids) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        String query = new Select(clazz).buildQuery(Map.of("id", ids));
        return jdbcTemplate.query(query, rowMapper);
    }

    public <T> Optional<T> load(Class<T> clazz, Long id) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        String query = new SelectByPrimaryKey(clazz).buildQuery(id);
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }
}
