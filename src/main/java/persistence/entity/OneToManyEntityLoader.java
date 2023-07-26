package persistence.entity;

import jdbc.RowMapper;
import jdbc.exception.RowMapException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static persistence.sql.util.StringConstant.CHILD_PREFIX;
import static persistence.sql.util.StringConstant.PARENT_PREFIX;

public class OneToManyEntityLoader<T> implements RowMapper<T> {
    private final EntityMeta parentMeta;
    private final Map<String, Field> parentFieldsByName;
    private final Map<String, Field> childFieldsByName;
    private final Map<EntityKey, T> parentObjectByKey = new HashMap<>();


    public OneToManyEntityLoader(Class<T> clazz) {
        this.parentMeta = new EntityMeta(clazz);
        this.parentFieldsByName = parentMeta.collectColumnFields(PARENT_PREFIX);
        this.childFieldsByName = parentMeta.getChildMeta().collectColumnFields(CHILD_PREFIX);
    }

    public List<T> collectDistinct() {
        return new ArrayList<>(parentObjectByKey.values());
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            Object parent = createParent(resultSet);
            addChild(resultSet, parent);
            return (T) parent;
        } catch (Exception e) {
            throw new RowMapException(e);
        }
    }

    private T createParent(ResultSet resultSet) throws Exception {
        T parent = (T) createObject(
                parentMeta,
                parentFieldsByName,
                resultSet
        );
        EntityKey key = new EntityKey(parent);
        if (parentObjectByKey.get(key) == null) {
            parentObjectByKey.put(key, parent);
        }
        return parentObjectByKey.get(key);
    }

    private void addChild(ResultSet resultSet, Object parent) throws Exception {
        Object child = createObject(
                parentMeta.getChildMeta(),
                childFieldsByName,
                resultSet
        );
        parentMeta.addChild(parent, child);
    }

    private Object createObject(
            EntityMeta meta,
            Map<String, Field> fieldsByName,
            ResultSet resultSet
    ) throws Exception {
        final Object obj = meta.getParentClass()
                .getDeclaredConstructor()
                .newInstance();
        for (Map.Entry<String, Field> entry : fieldsByName.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();
            field.setAccessible(true);
            field.set(obj, resultSet.getObject(columnName));
        }
        meta.initOneToMany(obj);
        return obj;
    }
}
