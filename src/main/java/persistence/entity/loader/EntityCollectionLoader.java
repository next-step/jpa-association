package persistence.entity.loader;

import java.lang.reflect.Field;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.meta.ColumnMeta;
import persistence.meta.RelationMeta;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.dml.query.WhereOperator;
import persistence.sql.dml.query.builder.SelectQueryBuilder;

public class EntityCollectionLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityCollectionLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> List<T> load(Class<T> fieldClazz, Field field, Object id) {
        ColumnMeta columnMeta = new ColumnMeta(field);
        RelationMeta relationMeta = columnMeta.relationMeta();
        String query = SelectQueryBuilder.builder()
                .select()
                .from(relationMeta.getJoinTableName())
                .where(List.of(
                        new WhereCondition(
                                relationMeta.getJoinColumnName(),
                                WhereOperator.EQUAL,
                                id))
                )
                .build();

        List<?> children = jdbcTemplate.query(query, new EntityRowMapper<>(relationMeta.getJoinColumnType()));
        return children.stream()
                .map(fieldClazz::cast)
                .toList();
    }

}
