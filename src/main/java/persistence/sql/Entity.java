package persistence.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Entity {
    protected static final String SINGLE_QUOTE = "'";

    private final Class<?> clazz;
    private final Object instance;
    private final Id id;
    private final Columns columns;

    public Entity(Object entity) {
        if (entity == null) {
            throw new IllegalStateException("Entity is not set");
        }
        final Class<?> clazz = entity.getClass();
        this.clazz = clazz;
        this.instance = entity;
        this.id = new Id(entity.getClass());
        this.columns = new Columns(clazz);
        this.columns.addValues(entity);
    }

    public Object getEntity() {
        try {
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            final Object entity = declaredConstructor.newInstance();
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = columns.getColumnValues().get(field.getName());
                if (value == null) {
                    field.set(entity, null);
                    continue;
                }
                field.set(entity, value);

            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot create new instance of " + clazz.getName());
        }
    }

    public Map<String, String> valuesByField() {
        Map<String, String> values = new LinkedHashMap<>();
        columns.getColumns().values().forEach(e -> values.put(e.getName(), convertValue(instance, e)));
        return values;
    }

    public List<String> getValues() {
        return valuesByField()
                .values()
                .stream()
                .toList();
    }

    private String convertValue(Object entity, Field field) {
        try {
            field.setAccessible(true);
            final Object value = field.get(entity);
            if (value == null) {
                return null;
            }
            if (field.getType().equals(String.class)) {
                return SINGLE_QUOTE + value + SINGLE_QUOTE;
            }
            return value.toString();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot get value from field " + field.getName());
        }
    }

    public Id getId() {
        return id;
    }

    public Map<String, Field> getColumns() {
        return columns.getColumns();
    }
}
