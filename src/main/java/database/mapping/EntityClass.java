package database.mapping;

import database.dialect.MySQLDialect;
import jdbc.RowMapper;

public class EntityClass {
    private final Class<?> clazz;
    private final RowMapper<Object> rowMapper;

    private EntityClass(Class<?> clazz, RowMapper<Object> rowMapper) {
        this.clazz = clazz;
        this.rowMapper = rowMapper;
    }

    public static EntityClass of(Class<?> clazz) {
        RowMapper<Object> rowMapper = RowMapperFactory.create(clazz, MySQLDialect.getInstance());
        return new EntityClass(clazz, rowMapper);
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
