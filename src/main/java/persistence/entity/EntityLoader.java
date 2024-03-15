package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.BooleanExpression;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereBuilder;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(Class<T> clazz, Object id) {
        TableData table = TableData.from(clazz);
        Columns columns = Columns.createColumns(clazz);

        if(columns.hasAssociation()) {
            CollectionLoader collectionLoader = new CollectionLoader(jdbcTemplate, table, columns);
            return collectionLoader.load(clazz, id);
        }

        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(table, columns);
        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(BooleanExpression.eq(columns.getPkColumnName(), id));
        String query = selectQueryBuilder.build(whereBuilder, null);
        return jdbcTemplate.queryForObject(query, new DefaultRowMapper<T>(clazz));
    }
}
