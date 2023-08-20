package fixture;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;
import persistence.sql.ddl.builder.DdlQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class TableFixtures {
    public static void createTable(Class<?> clazz, JdbcTemplate jdbcTemplate) {
        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(clazz);
        String createQuery = ddlQueryBuilder.create();
        jdbcTemplate.execute(createQuery);
    }

    public static void dropTable(Class<?> clazz, JdbcTemplate jdbcTemplate) {
        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(clazz);
        String dropQuery = ddlQueryBuilder.drop();
        jdbcTemplate.execute(dropQuery);
    }

    public static void insert(Object object, JdbcTemplate jdbcTemplate) {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(object.getClass());
        String insertQuery = InsertQueryBuilder.INSTANCE.insert(entityMeta, object);
        jdbcTemplate.execute(insertQuery);
    }

    public static <T> T select(Class<T> clazz, Long id, JdbcTemplate jdbcTemplate) {
        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(clazz);
        String findByIdQuery = selectQueryBuilder.findById(entityMeta, id);
        return jdbcTemplate.queryForObject(findByIdQuery, new RowMapperImpl<>(clazz));
    }
}
