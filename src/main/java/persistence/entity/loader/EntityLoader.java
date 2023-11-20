package persistence.entity.loader;

import java.sql.ResultSet;

public interface EntityLoader {
    <T> T load(Class<T> tClass, ResultSet resultSet);
}
