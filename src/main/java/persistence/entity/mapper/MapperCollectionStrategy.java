package persistence.entity.mapper;

import java.util.Collection;

@FunctionalInterface
public interface MapperCollectionStrategy {
    Collection<Object> createCollectionBy(Class<?> type);
}
