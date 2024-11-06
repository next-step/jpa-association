package persistence;

import builder.dml.EntityData;
import builder.dml.JoinEntityData;
import builder.dml.builder.SelectAllQueryBuilder;
import builder.dml.builder.SelectByIdQueryBuilder;
import jdbc.EntityMapper;
import jdbc.JdbcTemplate;

import java.util.List;

public class EntityLoader {

    private final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //데이터를 조회한다.
    public <T> T find(Class<T> clazz, Object id) {
        EntityData entityData = EntityData.createEntityData(clazz, id);
        return jdbcTemplate.queryForObject(selectByIdQueryBuilder.buildQuery(entityData), resultSet -> EntityMapper.mapRow(resultSet, entityData));
    }

    //Lazy 데이터를 전체 조회한다.
    public <T> List<T> findByIdLazy(JoinEntityData joinEntityData) {
        return (List<T>) jdbcTemplate.query(selectByIdQueryBuilder.buildLazyQuery(joinEntityData), resultSet -> EntityMapper.mapRow(resultSet, joinEntityData.getClazz()));
    }

}
