package persistence.entity.loader;

import java.util.List;
import java.util.Map;
import persistence.sql.meta.Column;

public interface EntityLoader {

    <T> T find(Class<T> clazz, Long id);

    <T> List<T> find(Class<T> clazz, Map<Column, Object> conditions);
}
