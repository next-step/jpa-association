package persistence.entity.database;

import database.dialect.MySQLDialect;
import database.mapping.EntityClass;
import database.mapping.RowMapperFactory;
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

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> List<T> load(Class<T> clazz, Collection<Long> ids) {
        EntityClass<T> entityClass = EntityClass.of(clazz);
        RowMapper<T> rowMapper = entityClass.getRowMapper();

        String query = new Select(clazz).buildQuery(Map.of("id", ids));
        return jdbcTemplate.query(query, rowMapper);
    }

    public <T> Optional<T> load(Class<T> clazz, Long id) {
        RowMapper<T> rowMapper = RowMapperFactory.create(clazz, MySQLDialect.getInstance());

        String query = new SelectByPrimaryKey(clazz).buildQuery(id);
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }
}
