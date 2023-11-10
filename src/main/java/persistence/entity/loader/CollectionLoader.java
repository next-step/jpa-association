package persistence.entity.loader;

import java.util.List;

public interface CollectionLoader {
    <T> List<T> loadCollection(Class<T> clazz, String columnName, String id);
}
