package persistence.entity.Proxy;

import persistence.entity.loader.CollectionEntityLoader;

import java.util.Collection;

public interface ProxyFactory {

    <T> Collection<T> generateCollectionProxy(final Class<?> fieldType, final CollectionEntityLoader collectionEntityLoader, final Class<T> joinedEntityClass, final String joinedTableSelectQuery);

}
