package database.mapping;

import database.dialect.MySQLDialect;
import jdbc.RowMapper;

import java.lang.reflect.Constructor;

public class EntityClass {
    private final Class<?> clazz;
    private final RowMapper<Object> rowMapper;

    private EntityClass(Class<?> clazz, RowMapper<Object> rowMapper) {
        this.clazz = clazz;
        this.rowMapper = rowMapper;
    }

    public static EntityClass of(Class<?> clazz) {
        Constructor<?> declaredConstructor;
        try {
            declaredConstructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        return new EntityClass(
                clazz,
                RowMapperFactory.create(declaredConstructor, clazz, MySQLDialect.getInstance())
        );
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return clazz.getName();
    }

    public RowMapper<Object> getRowMapper() {
        return rowMapper;
    }

}
