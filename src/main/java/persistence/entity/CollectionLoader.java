package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.dml.BooleanExpression;
import persistence.sql.dml.JoinBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereBuilder;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.OneToManyData;
import persistence.sql.mapping.TableData;

import java.util.List;


public class CollectionLoader {
    private final JdbcTemplate jdbcTemplate;
    private final TableData table;
    private final Columns columns;
    private static final Logger logger = LoggerFactory.getLogger(CollectionLoader.class);


    public CollectionLoader(JdbcTemplate jdbcTemplate, TableData table, Columns columns) {
        this.columns = columns;
        this.table = table;
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T load(Class<T> clazz, Object id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(table, columns);

        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(BooleanExpression.eq(columns.getPkColumnName(), id));
        List<OneToManyData> associations = columns.getEagerAssociations();

        if (associations.stream().noneMatch(OneToManyData::isLazyLoad)) {
            JoinBuilder joinBuilder = new JoinBuilder(table, columns);
            String query = selectQueryBuilder.build(whereBuilder, joinBuilder);
            return jdbcTemplate.queryForObject(query, new DefaultRowMapper<T>(clazz));
        }

        // TODO: LAZY
        String query = selectQueryBuilder.build(whereBuilder, null);
        return jdbcTemplate.queryForObject(query, new DefaultRowMapper<T>(clazz));
    }
}
