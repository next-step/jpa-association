package persistence.entity.loader;

import java.sql.ResultSet;
import java.util.List;
import persistence.meta.EntityMeta;

public class OneToManyLazyMapper extends EntityMapper {
    public OneToManyLazyMapper(EntityMeta entityMeta) {
        super(entityMeta);
    }

    @Override
    public <T> T findMapper(Class<T> tClass, ResultSet resultSet) {
        return null;
    }

    @Override
    public <T> List<T> findAllMapper(Class<T> tClass, ResultSet resultSet) {
        return null;
    }
}

