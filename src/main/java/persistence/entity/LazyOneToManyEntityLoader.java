package persistence.entity;

import jdbc.RowMapper;
import jdbc.exception.RowMapException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class LazyOneToManyEntityLoader<T> implements RowMapper<T> {
    private final EntityMeta meta;
    private final EntitiesLoader childrenLoader;

    public LazyOneToManyEntityLoader(Class clazz, EntitiesLoader childrenLoader) {
        this.meta = new EntityMeta(clazz);
        this.childrenLoader = childrenLoader;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        T object = createObject(resultSet);
        setProxy(object);
        return object;
    }

    private T createObject(ResultSet resultSet) {
        try {
            final T object = (T) meta.getParentClass().getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Field> entry : meta.collectColumnFields().entrySet()) {
                String columnName = entry.getKey();
                Field field = entry.getValue();
                Object value = resultSet.getObject(columnName);
                field.setAccessible(true);
                field.set(object, value);
            }
            return object;
        } catch (Exception e) {
            throw new RowMapException(e);
        }
    }

    private void setProxy(T object) {
        Field fkField = meta.getFkField();
        try {
            Enhancer enhancer = getEnhancerForChild(object);
            fkField.setAccessible(true);
            fkField.set(object, enhancer.create());
        } catch (IllegalAccessException e) {
            throw new RowMapException(e);
        }
    }

    private Enhancer getEnhancerForChild(T object) throws IllegalAccessException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        Field pkField = meta.getPkField();
        pkField.setAccessible(true);
        Map<String, Object> condition = Map.of(
                meta.getFkName(),
                pkField.get(object)
        );
        enhancer.setCallback(
                (LazyLoader) () -> childrenLoader.findAllBy(condition)
        );
        return enhancer;
    }
}
