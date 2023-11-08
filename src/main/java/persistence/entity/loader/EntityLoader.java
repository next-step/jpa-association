package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.core.EntityIdColumn;
import persistence.core.EntityMetadata;
import persistence.core.EntityOneToManyColumn;
import persistence.entity.mapper.EntityRowMapper;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;
import java.util.Optional;

public class EntityLoader<T> {
    private final EntityMetadata<T> entityMetadata;
    private final String tableName;
    private final EntityIdColumn idColumn;
    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;
    private final EntityRowMapper<T> entityRowMapper;

    private EntityLoader(final EntityMetadata<T> entityMetadata, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        this.entityMetadata = entityMetadata;
        this.tableName = entityMetadata.getTableName();
        this.idColumn = entityMetadata.getIdColumn();
        this.dmlGenerator = dmlGenerator;
        this.jdbcTemplate = jdbcTemplate;
        this.entityRowMapper = EntityRowMapper.of(entityMetadata);
    }

    public static <T> EntityLoader<T> of(final EntityMetadata<T> entityMetadata, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        return new EntityLoader<>(entityMetadata, dmlGenerator, jdbcTemplate);
    }

    public Optional<T> loadById(final Object id) {
        final String query = renderSelect(id);
        final List<T> result = jdbcTemplate.query(query, entityRowMapper::mapRow);

        if (result.size() > 1) {
            throw new PersistenceException("id 로 조회된 row 가 2개 이상입니다.");
        }

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    public List<T> loadAllByOwnerId(final String ownerColumnName, final Object ownerId) {
        final String query = renderSelectByOwnerId(ownerColumnName, ownerId);
        return jdbcTemplate.query(query, entityRowMapper::mapRow);
    }

    public String renderSelect(final Object id) {
        final SelectQueryBuilder queryBuilder = dmlGenerator.select()
                .table(tableName)
                .column(entityMetadata.getColumnNamesWithAlias());

        entityMetadata.getEagerOneToManyColumns()
                .forEach(entityOneToManyColumn -> bindOneToManyJoinClause(queryBuilder, entityOneToManyColumn));

        return queryBuilder
                .where(idColumn.getNameWithAlias(), String.valueOf(id))
                .build();
    }

    private void bindOneToManyJoinClause(final SelectQueryBuilder queryBuilder, final EntityOneToManyColumn entityOneToManyColumn) {
        queryBuilder.leftJoin(entityOneToManyColumn.getAssociatedEntityTableName())
                .on(entityMetadata.getIdColumnNameWithAlias(), entityOneToManyColumn.getNameWithAliasAssociatedEntity());
    }

    public String renderSelectByOwnerId(final String ownerColumnName, final Object ownerId) {
        return dmlGenerator.select()
                .table(tableName)
                .column(entityMetadata.getColumnNamesWithAlias())
                .where(ownerColumnName, String.valueOf(ownerId))
                .build();
    }
}
