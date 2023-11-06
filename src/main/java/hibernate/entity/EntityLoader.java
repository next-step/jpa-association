package hibernate.entity;

import hibernate.dml.SelectQueryBuilder;
import hibernate.entity.meta.EntityClass;
import jdbc.JdbcTemplate;
import jdbc.ReflectionRowMapper;

import java.util.List;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    public EntityLoader(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(final EntityClass<T> entityClass, final Object id) {
        final String query = selectQueryBuilder.generateQuery(
                entityClass.tableName(),
                entityClass.getFieldNames(),
                entityClass.getEntityId(),
                id,
                entityClass.getEagerJoinTableFields(),
                entityClass.getEagerJoinTableIds()
        );
        return jdbcTemplate.queryForObject(query, ReflectionRowMapper.getInstance(entityClass));
    }

    public <T> List<T> findAll(final EntityClass<T> entityClass) {
        final String query = selectQueryBuilder.generateAllQuery(entityClass.tableName(), entityClass.getFieldNames());
        return jdbcTemplate.query(query, ReflectionRowMapper.getInstance(entityClass));
    }
}
