package persistence.sql.entity.collection;

import java.util.List;

public interface CollectionLoader {

    <T> List<T> findById(Class<T> clazz, Object id);

}
