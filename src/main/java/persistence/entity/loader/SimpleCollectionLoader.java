package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.util.List;

public class SimpleCollectionLoader implements CollectionLoader {
    private final JdbcTemplate jdbcTemplate;
    private final LoaderMapper loaderHelper;

    public SimpleCollectionLoader(JdbcTemplate jdbcTemplate) {
        this.loaderHelper = new LoaderMapper(this);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> loadCollection(EntityAttribute entityAttribute, String queryColumnName, String queryValue) {
        String sql = SelectQueryBuilder.of(entityAttribute)
                .where(entityAttribute.getTableName(), queryColumnName, queryValue)
                .prepareStatement();

        return jdbcTemplate.queryList(sql,
                rs -> loaderHelper.mapResultSetToList(entityAttribute, rs));
    }
}
