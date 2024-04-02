package persistence;

import persistence.sql.QueryException;

import java.lang.reflect.*;
import java.util.*;

public class ReflectionUtils {

    private ReflectionUtils() {
    }

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

    public static <T> Object getFieldValue(final Field field, final T object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    public static <T> void setFieldValue(final Field field, final T object, final Object value) {
        try {
            if (isCollectionType(field)) {
                setCollectionFieldValue(field, object, value);
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

    private static <T> void setCollectionFieldValue(final Field field, final T object, final Object value) {
        try {
            field.setAccessible(true);
            Object list = field.get(object);

            if (Objects.isNull(list)) {
                list = new ArrayList<>();
                field.set(object, list);
            }

            if (Objects.isNull(value) || !isDefinedObject(value)) {
                return;
            }

            ((List) list).add(value);
        } catch (IllegalAccessException e) {
            throw new QueryException("can't set field " + field.getName() + " at " + object.getClass().getName());
        } finally {
            field.setAccessible(false);
        }
    }

    public static <T> void setCollectionFieldValue(final Field field, final T object, final Collection<?> collection) {
        try {
            field.setAccessible(true);

            if (!isCollectionType(field)) {
                throw new RuntimeException(field.getName() + " is not list type");
            }

            field.set(object, collection);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    private static boolean isCollectionType(final Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    public static boolean isDefinedObject(final Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .anyMatch(field -> isDefinedField(field, object));
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

    private static String getClassNameByType(final Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            return typeArguments[0].getTypeName();
        }

        return type.getTypeName();
    }

    public static String mapToGenericClassName(final Field field) {
        return getClassNameByType(field.getGenericType());
    }

    public static Class<?> mapToGenericClass(final Field field) {
        try {
            return Class.forName(getClassNameByType(field.getGenericType()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
