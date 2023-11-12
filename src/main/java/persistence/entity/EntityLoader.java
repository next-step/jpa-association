package persistence.entity;

import java.util.List;

public interface EntityLoader {
    <T> T find(Class<T> tClass, Object id);

    <T> List<T> findAll(Class<T> tClass);
}
