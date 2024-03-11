package persistence.entity.database;

import database.mapping.EntityClass;
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

    public List<Object> load(Class<?> clazz, Collection<Long> ids) {
        EntityClass entityClass = EntityClass.of(clazz);
        RowMapper<Object> rowMapper = entityClass.getRowMapper();

        String query = new Select(clazz).buildQuery(Map.of("id", ids));
        return jdbcTemplate.query(query, rowMapper);
    }

    public Optional<Object> load(Class<?> clazz, Long id) {
        EntityClass entityClass = EntityClass.of(clazz);
        RowMapper<Object> rowMapper = entityClass.getRowMapper();

        String query = new SelectByPrimaryKey(clazz).buildQuery(id);
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }
}
