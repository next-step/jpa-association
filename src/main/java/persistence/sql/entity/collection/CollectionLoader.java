package persistence.sql.entity.collection;

import java.util.List;

public interface CollectionLoader {

    List<Object> findById(Class<?> clazz, Object id);

}
