package persistence.sql.entity.collection;

import net.sf.cglib.proxy.Enhancer;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.exception.InvalidProxyException;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.proxy.LazyLoadingProxy;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class LazyLoadingManager {

    private final CollectionPersister collectionPersister;
    private final CollectionLoader collectionLoader;

    public LazyLoadingManager(final CollectionPersister collectionPersister,
                              final CollectionLoader collectionLoader) {
        this.collectionPersister = collectionPersister;
        this.collectionLoader = collectionLoader;
    }

    public <T> T setLazyLoading(final T entity,
                                final EntityMappingTable entityMappingTable) {
        List<DomainType> fetchTypeDomainType = entityMappingTable.getFetchType();

        fetchTypeDomainType
                .forEach(domainType -> {
                    Class<?> subEntityType = collectionClass(domainType.getField());
                    Object lazyProxy = Enhancer.create(
                            subEntityType,
                            new LazyLoadingProxy(
                                    collectionPersister,
                                    collectionLoader,
                                    subEntityType,
                                    domainType.getValue())
                    );

                    setField(domainType.getField(), entity, lazyProxy);
                });

        return entity;
    }

    private void setField(Field field, Object entity, Object proxy) {
        try {
            field.set(entity, proxy);
        } catch (Exception e) {
            throw new InvalidProxyException();
        }
    }

    private Class<?> collectionClass(Field field) {
        Type type = field.getGenericType();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments != null && typeArguments.length > 0) {
                Type typeArgument = typeArguments[0];
                try {
                    return Class.forName(typeArgument.getTypeName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException();
    }

}
