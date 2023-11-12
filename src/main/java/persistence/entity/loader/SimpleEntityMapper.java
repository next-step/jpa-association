package persistence.entity.loader;

import java.sql.ResultSet;
import persistence.meta.EntityMeta;

public class SimpleEntityMapper extends EntityMapper {

    public SimpleEntityMapper(EntityMeta entityMeta) {
        super(entityMeta);
    }

    @Override
    public <T> T findMapper(Class<T> tClass, ResultSet resultSet) {
        return resultSetToEntity(tClass, resultSet);
    }

}
