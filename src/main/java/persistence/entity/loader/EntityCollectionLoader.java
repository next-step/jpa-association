package persistence.entity.loader;

import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.core.EntityOneToManyColumn;
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
        final EntityLoader<?> entityLoader = new EntityLoader<>(oneToManyColumn.getJoinColumnType(), dmlGenerator, jdbcTemplate);
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(oneToManyColumn.getType());
        enhancer.setCallback(getLazyLoader(oneToManyColumn.getNameWithAliasAssociatedEntity(), joinColumnId, entityLoader));
        return enhancer.create();
    }

    private LazyLoader getLazyLoader(final String joinColumnName, final Object joinColumnId, final EntityLoader<?> entityLoader) {
        return () -> entityLoader.loadAll(joinColumnName, joinColumnId);
    }
}
