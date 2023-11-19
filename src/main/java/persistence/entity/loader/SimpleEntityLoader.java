package persistence.entity.loader;

import java.sql.ResultSet;

public class SimpleEntityLoader extends AbstractEntityLoader {
    private SimpleEntityLoader() {};
    public static SimpleEntityLoader create() {
        return new SimpleEntityLoader();
    }

    public <T> T load(Class<T> tClass, ResultSet resultSet) {
        return resultSetToEntity(tClass, resultSet);
    }
}
