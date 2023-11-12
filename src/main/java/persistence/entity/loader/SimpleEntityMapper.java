package persistence.entity.loader;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import persistence.meta.EntityMeta;

public class SimpleEntityMapper extends EntityMapper {

    public SimpleEntityMapper(EntityMeta entityMeta) {
        super(entityMeta);
    }

    @Override
    public <T> T findMapper(Class<T> tClass, ResultSet resultSet) {
        return resultSetToEntity(tClass, resultSet);
    }

    @Override
    public <T> List<T> findAllMapper(Class<T> tClass, ResultSet resultSet) {
        final List<T> list = new ArrayList<>();
        while (isNextRow(resultSet)) {
            final T instance = resultSetToEntity(tClass, resultSet);
            list.add(instance);
        }
        return list;
    }
}
