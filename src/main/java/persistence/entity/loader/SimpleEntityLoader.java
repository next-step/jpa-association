package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class SimpleEntityLoader implements EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final EntityAttributes entityAttributes;
    private final LoaderMapper loaderMapper;

    public SimpleEntityLoader(JdbcTemplate jdbcTemplate, EntityAttributes entityAttributes) {
        this.loaderMapper = new LoaderMapper(entityAttributes, new SimpleCollectionLoader(jdbcTemplate, entityAttributes));
        this.jdbcTemplate = jdbcTemplate;
        this.entityAttributes = entityAttributes;
    }

    @Override
    public <T> T load(EntityAttribute entityAttribute, String columnName, String id) {

        String sql = SelectQueryBuilder.of(entityAttribute)
                .where(entityAttribute.getTableName(), columnName, id)
                .prepareStatement();

        return jdbcTemplate.queryForObject(sql,
                rs -> loaderMapper.mapResultSetToEntity(entityAttribute, rs));
    }
}
