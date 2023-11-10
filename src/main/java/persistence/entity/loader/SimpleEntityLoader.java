package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class SimpleEntityLoader implements EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final EntityAttributes entityAttributes;
    private final LoaderMapper loaderHelper;

    public SimpleEntityLoader(JdbcTemplate jdbcTemplate, EntityAttributes entityAttributes) {
        this.loaderHelper = new LoaderMapper(entityAttributes, new SimpleCollectionLoader(jdbcTemplate, entityAttributes));
        this.jdbcTemplate = jdbcTemplate;
        this.entityAttributes = entityAttributes;
    }

    @Override
    public <T> T load(Class<T> clazz, String columnName, String id) {
        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(clazz);

        String sql = SelectQueryBuilder.of(entityAttribute)
                .where(entityAttribute.getTableName(), columnName, id)
                .prepareStatement();

        return jdbcTemplate.queryForObject(sql,
                rs -> loaderHelper.mapResultSetToEntity(clazz, rs));
    }
}
