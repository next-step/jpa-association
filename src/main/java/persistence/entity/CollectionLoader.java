package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.dml.BooleanExpression;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereBuilder;
import persistence.sql.mapping.Associations;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import java.util.List;


public class CollectionLoader {
    private final JdbcTemplate jdbcTemplate;
    private final TableData table;
    private final Columns columns;
    private final Associations associations;
    private static final Logger logger = LoggerFactory.getLogger(CollectionLoader.class);


    public CollectionLoader(
            JdbcTemplate jdbcTemplate,
            TableData table,
            Columns columns,
            Associations associations
    ) {
        this.columns = columns;
        this.table = table;
        this.jdbcTemplate = jdbcTemplate;
        this.associations = associations;
    }

    public <T> T load(Class<T> clazz, Object id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(table, columns, associations);

        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(BooleanExpression.eq(columns.getPkColumnName(), id));
        String query = selectQueryBuilder.build(whereBuilder);

        T entity = jdbcTemplate.queryForObject(query, new DefaultRowMapper<T>(clazz));

        if (associations.hasLazyLoad()) {
            setPersistentCollection(entity);
        }

        return entity;
    }

    private <T> void setPersistentCollection(T entity) {
        associations.getLazyAssociations().forEach(association -> {
            Class<?> collectionType = association.getField().getType();
            if (collectionType == List.class) {
                association.setCollectionToField(entity, new PersistentList<>(this));
            } else {
                throw new UnsupportedOperationException("Unsupported collection type: " + collectionType.getSimpleName());
            }
        });
    }
}
