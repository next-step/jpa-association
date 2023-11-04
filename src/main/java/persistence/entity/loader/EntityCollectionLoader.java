package persistence.entity.loader;

import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityOneToManyColumn;
import persistence.entity.mapper.EntityRowMapper;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

public class EntityCollectionLoader {

    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;

    public EntityCollectionLoader(final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        this.dmlGenerator = dmlGenerator;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void initLazyOneToMany(final EntityOneToManyColumn oneToManyColumn, final Object targetEntity, final Object joinColumnId) {
        if (oneToManyColumn.isFetchTypeLazy()) {
            final String oneToManyFieldName = oneToManyColumn.getFieldName();
            final Object proxyOneToManyFieldValue = createProxy(oneToManyColumn, joinColumnId);
            ReflectionUtils.injectField(targetEntity, oneToManyFieldName, proxyOneToManyFieldValue);
        }
    }

    private Object createProxy(final EntityOneToManyColumn oneToManyColumn, final Object joinColumnId) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(oneToManyColumn.getType());
        enhancer.setCallback(getLazyLoader(oneToManyColumn, joinColumnId));
        return enhancer.create();
    }

    private LazyLoader getLazyLoader(final EntityOneToManyColumn oneToManyColumn, final Object joinColumnId) {
        return () -> {
            final Class<?> associatedEntityClassType = oneToManyColumn.getJoinColumnType();
            final EntityMetadata<?> associatedEntityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(associatedEntityClassType);
            final String query = selectByOwnerId(associatedEntityMetadata, oneToManyColumn.getNameWithAliasAssociatedEntity(), joinColumnId);
            return jdbcTemplate.query(query, new EntityRowMapper<>(associatedEntityClassType)::mapRow);
        };
    }

    public String selectByOwnerId(final EntityMetadata<?> associatedEntityMetadata, final String joinColumnName, final Object joinColumnId) {
        return dmlGenerator.select()
                .table(associatedEntityMetadata.getTableName())
                .column(associatedEntityMetadata)
                .where(joinColumnName, String.valueOf(joinColumnId))
                .build();
    }
}
