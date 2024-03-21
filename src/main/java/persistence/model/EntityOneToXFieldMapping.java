package persistence.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public abstract class EntityOneToXFieldMapping extends EntityJoinFieldMapping {

    @Override
    public Class<?> getEntityType(final Field field) {
        return (Class<?>) (((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
    }

}
