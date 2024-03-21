package persistence;

import persistence.sql.QueryException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReflectionUtils {

    private ReflectionUtils() {}

    public static <T> T createInstance(final Class<T> clazz) {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            final int parameterCount = constructor.getParameterCount();
            final Object[] parameters = new Object[parameterCount];

            return constructor.newInstance(parameters);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new QueryException("can't create " + clazz.getName() + " instance");
        } catch (NoSuchMethodException e) {
            throw new QueryException("not found " + clazz.getName() + " constructor");
        }
    }

    public static <T> void setFieldValue(final Field field, final T object, final Object value) {
        try {
            if (isListType(field)) {
                setListFieldValue(field, object, value);
                return;
            }

            if (isDefinedField(field, object)) {
                return;
            }

            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new QueryException("can't set field " + field.getName() + " at " + object.getClass().getName());
        }
    }

    private static <T> void setListFieldValue(final Field field, final T object, final Object value) {
        try {
            field.setAccessible(true);
            Object list = field.get(object);

            if (Objects.isNull(list)) {
                list = new ArrayList<>();
                field.set(object, list);
            }

            if (Objects.isNull(value)) {
                return;
            }

            ((List) list).add(value);
        } catch (IllegalAccessException e) {
            throw new QueryException("can't set field " + field.getName() + " at " + object.getClass().getName());
        } finally {
            field.setAccessible(false);
        }
    }

    private static boolean isListType(final Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    private static boolean isDefinedField(final Field field, final Object object) {
        try {
            field.setAccessible(true);
            final Object value = field.get(object);

            return Objects.nonNull(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    private static Object getFieldValue(final Field field, final Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    public static boolean isDifferentFieldValue(final Field field, final Object object, final Object value) {
        return !Objects.deepEquals(getFieldValue(field, object), value);
    }
}
