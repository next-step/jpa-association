package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.util.List;

public class SimpleCollectionLoader implements CollectionLoader {
    private final JdbcTemplate jdbcTemplate;
    private final EntityAttributes entityAttributes;
    private final LoaderHelper loaderHelper;

    public SimpleCollectionLoader(JdbcTemplate jdbcTemplate, EntityAttributes entityAttributes) {
        this.loaderHelper = new LoaderHelper(entityAttributes, this);
        this.jdbcTemplate = jdbcTemplate;
        this.entityAttributes = entityAttributes;
    }

    @Override
    public <T> List<T> loadCollection(Class<T> clazz, String columnName, String id) {
        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(clazz);

        String sql = SelectQueryBuilder.of(entityAttribute)
                .where(entityAttribute.getTableName(), columnName, id)
                .prepareStatement();

        return jdbcTemplate.queryList(sql,
                rs -> loaderHelper.mapResultSetToList(clazz, rs));
    }
}
