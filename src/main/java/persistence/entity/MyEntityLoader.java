package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import jdbc.RowMapperFactory;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.meta.Table;

public class MyEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;

    public MyEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = SelectQueryBuilder.getInstance();
    }

    @Override
    public <T> T find(Class<T> clazz, Object Id) {
        Table table = Table.from(clazz);
        if (!table.containsAssociation()) {
            String query = selectQueryBuilder.build(table, Id);
            RowMapper<T> rowMapper = RowMapperFactory.create(clazz);
            return jdbcTemplate.queryForObject(query, rowMapper);
        }
        String query = selectQueryBuilder.buildWithJoin(table, Id);
        RowMapper<T> rowMapper = RowMapperFactory.create(clazz);
        return jdbcTemplate.queryForObject(query, rowMapper);
    }
}
