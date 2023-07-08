package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T load(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;
        String selectQuery = selectQueryBuilder.findById(clazz, id);
        return jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(clazz));
    }
}
