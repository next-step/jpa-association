package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.CustomSelectQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import pojo.EntityJoinMetaData;
import pojo.EntityMetaData;

import java.util.List;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaData entityMetaData;

    public EntityLoaderImpl(JdbcTemplate jdbcTemplate, EntityMetaData entityMetaData) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaData = entityMetaData;
    }

    @Override
    public <T> T findById(Class<T> clazz, Object entity, Object condition) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityMetaData);
        return jdbcTemplate.queryForObject(selectQueryBuilder.findByIdQuery(entity, clazz, condition), new RowMapperImpl<>(clazz));
    }

    public <T> List<T> findAll(Class<T> clazz) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityMetaData);
        return jdbcTemplate.query(selectQueryBuilder.findAllQuery(), new RowMapperImpl<>(clazz));
    }

    //연관관계가 있는 경우 & eager 타입만 고려
    @Override
    public <T> List<T> findByIdWithAssociation(Class<T> clazz, Object entity, Object condition) {
        CustomSelectQueryBuilder customSelectQueryBuilder = new CustomSelectQueryBuilder(entityMetaData);

        EntityJoinMetaData entityJoinMetaData = entityMetaData.getEntityJoinMetaData();
        if (!entityJoinMetaData.isLazy()) {
            return eagerTypeQuery(customSelectQueryBuilder, clazz, entity);
        }

        //TODO 추후 lazy 타입 넣을 예정
        return jdbcTemplate.query(customSelectQueryBuilder.findByIdJoinQuery(entity, clazz), new RowMapperImpl<>(clazz));

    }

    private <T> List<T> eagerTypeQuery(CustomSelectQueryBuilder customSelectQueryBuilder, Class<T> clazz, Object entity) {
        return jdbcTemplate.query(customSelectQueryBuilder.findByIdJoinQuery(entity, clazz), new RowMapperImpl<>(clazz));

    }
}
