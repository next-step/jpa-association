package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class SimpleEntityLoader implements EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final LoaderMapper loaderMapper;

    public SimpleEntityLoader(JdbcTemplate jdbcTemplate) {
        this.loaderMapper = new LoaderMapper(new SimpleCollectionLoader(jdbcTemplate));
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T load(EntityAttribute entityAttribute, String queryColumnName, String queryValue) {

        String sql = SelectQueryBuilder.of(entityAttribute)
                .where(entityAttribute.getTableName(), queryColumnName, queryValue)
                .prepareStatement();

        return jdbcTemplate.queryForObject(sql,
                rs -> loaderMapper.mapResultSetToEntity(entityAttribute, rs));
    }
}
