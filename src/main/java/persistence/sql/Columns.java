package persistence.sql;

import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class Columns extends AbstractColumns {

    private final Map<String, Object> columnValues = new LinkedHashMap<>();

    public Columns(Class<?> clazz) {
        super(clazz);
    }

    public void addValues(Object instance) {
        if (instance == null) {
            throw new IllegalStateException("Entity is not set");
        }
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (addable(field)) {
                field.setAccessible(true);
                try {
                    columnValues.put(field.getName(), field.get(instance));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    @Override
    protected boolean addable(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    public Map<String, Object> getColumnValues() {
        return new LinkedHashMap<>(columnValues);
    }

}
