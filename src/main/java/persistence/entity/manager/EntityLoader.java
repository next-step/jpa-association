package persistence.entity.manager;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T load(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(clazz);
        String selectQuery = selectQueryBuilder.findById(entityMeta, id);
        return jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(clazz));
    }
}
