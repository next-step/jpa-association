package persistence.entity.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.collection.AbstractPersistentCollection;
import persistence.entity.collection.mapping.PersistentCollectionMappings;
import persistence.entity.loader.CollectionEntityLoader;
import persistence.model.PersistentClass;
import persistence.model.PersistentClassMapping;

import java.util.Collection;
import java.util.List;

public class CglibProxyFactory implements ProxyFactory {

    @Override
    public <T> Collection<T> generateCollectionProxy(final Class<?> fieldType, final CollectionEntityLoader collectionEntityLoader, final Class<T> joinedEntityClass, final String joinedTableSelectQuery) {
        final PersistentClass<?> persistentClass = PersistentClassMapping.getPersistentClass(joinedEntityClass);
        final LazyLoader lazyLoader = () -> {
            final List<?> values = collectionEntityLoader.queryWithLazyColumn(persistentClass, joinedTableSelectQuery);
            final AbstractPersistentCollection<?> collection = PersistentCollectionMappings.getInstance().findMapping(fieldType)
                    .createPersistentCollection();

            collection.addAll((Collection) values);

            return collection;
        };
        return (Collection<T>) generateEnhancer(fieldType, lazyLoader).create();
    }

    private Enhancer generateEnhancer(final Class<?> fieldType, final LazyLoader lazyLoader) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(fieldType);
        enhancer.setCallback(lazyLoader);
        return enhancer;
    }
}
