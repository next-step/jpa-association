package persistence;

import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JoinEntityLoader {
    private final Field[] fields;
    private List<Object> items;

    public JoinEntityLoader(Field[] fields) {
        this.fields = fields;
    }

    public <T> Field mapJoinFields(ResultSet resultSet) {
        List<Object> items = null;
        Field joinField = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                Class<?> fieldType = field.getType();
                if (List.class.isAssignableFrom(fieldType)) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Class<?> listItemType = (Class<?>) genericType.getActualTypeArguments()[0];

                    try {
                        if (items == null) {
                            items = makeOneToMany(resultSet, listItemType);
                            joinField = field;
                        } else {
                            items.addAll(makeOneToMany(resultSet, listItemType));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        this.items = items;
        return joinField;
    }

    private List<Object> makeOneToMany(ResultSet resultSet, Class<?> listItemType) throws SQLException {
        List<Object> items = new ArrayList<>();
        boolean isLoop = true;
        while (isLoop) {
            Object item;
            try {
                item = listItemType.getDeclaredConstructor().newInstance();
                mapDataFields(item, resultSet);
                items.add(item);

                if (!resultSet.next()) {
                    isLoop = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return items;
    }

    private void mapDataFields(Object object, ResultSet resultSet) throws SQLException {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            String columnName = field.getName();
            try {
                Object value = resultSet.getObject(columnName);
                field.setAccessible(true);
                field.set(object, value);
            } catch (Exception e) {
                continue;
            }
        }
    }

    public Object joinItems() {
        return items;
    }
}
