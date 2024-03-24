package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.entity.collection.CollectionLoader;
import persistence.entity.collection.PersistentList;
import persistence.sql.dml.BooleanExpression;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereBuilder;
import persistence.sql.mapping.Associations;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import java.util.List;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(Class<T> clazz, Object id) {
        TableData table = TableData.from(clazz);
        Columns columns = Columns.createColumns(clazz);
        Associations associations = Associations.fromEntityClass(clazz);

        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(table, columns, associations);
        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(BooleanExpression.eq(columns.getPkColumnName(), id));
        String query = selectQueryBuilder.build(whereBuilder);
        T entity = jdbcTemplate.queryForObject(query, new DefaultRowMapper<T>(clazz));

        if(associations.hasLazyLoad()) {
            setPersistentCollection(entity, id, associations);
        }

        return entity;
    }

    private <T> void setPersistentCollection(
            T entity,
            Object id,
            Associations associations
    ) {
        associations.getLazyAssociations().forEach(association -> {
            Class<?> collectionType = association.getField().getType();
            CollectionLoader collectionLoader = new CollectionLoader(jdbcTemplate, association);

            if (collectionType == List.class) {
                association.setCollectionToField(entity, new PersistentList<>(collectionLoader, id));
            } else {
                throw new UnsupportedOperationException("Unsupported collection type: " + collectionType.getSimpleName());
            }
        });
    }
}
