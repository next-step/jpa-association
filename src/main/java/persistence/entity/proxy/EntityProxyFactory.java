package persistence.entity.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.core.EntityAssociatedColumn;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.util.ReflectionUtils;

public class EntityProxyFactory {

    private final EntityLoaders entityLoaders;

    public EntityProxyFactory(final EntityLoaders entityLoaders) {
        this.entityLoaders = entityLoaders;
    }

    public void initProxy(final Object ownerId, final Object owner, final EntityAssociatedColumn proxyColumn) {
        final String proxyFieldName = proxyColumn.getFieldName();
        final Object proxyOneToManyFieldValue = createProxy(proxyColumn, ownerId);
        ReflectionUtils.injectField(owner, proxyFieldName, proxyOneToManyFieldValue);
    }

    private Object createProxy(final EntityAssociatedColumn proxyColumn, final Object joinColumnId) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyColumn.getType());
        enhancer.setCallback(getLazyLoader(proxyColumn, joinColumnId));
        return enhancer.create();
    }

    private LazyLoader getLazyLoader(final EntityAssociatedColumn proxyColumn, final Object joinColumnId) {
        return () -> {
            final Class<?> associatedEntityClassType = proxyColumn.getJoinColumnType();
            final EntityLoader<?> associatedEntityLoader = entityLoaders.getEntityLoader(associatedEntityClassType);
            return associatedEntityLoader.loadAllByOwnerId(proxyColumn.getNameWithAliasAssociatedEntity(), joinColumnId);
        };
    }

}
