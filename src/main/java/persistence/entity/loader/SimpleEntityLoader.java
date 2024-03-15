package persistence.entity.loader;

import java.util.List;
import java.util.Map;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.meta.Column;

public class SimpleEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;

    private SimpleEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = DmlGenerator.getInstance();
    }

    public static SimpleEntityLoader from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityLoader(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T t =  jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(clazz, id),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));

        return t;
    }

    @Override
    public <T> List<T> find(Class<T> clazz, Map<Column, Object> conditions) {
        return jdbcTemplate.query(dmlGenerator.generateSelectQuery(clazz, conditions),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }
}
