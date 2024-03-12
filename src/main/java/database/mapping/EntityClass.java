package database.mapping;

import database.dialect.MySQLDialect;
import jdbc.RowMapper;

public class EntityClass<T> {
    private final Class<T> clazz;
    private final RowMapper<T> rowMapper;

    private EntityClass(Class<T> clazz, RowMapper<T> rowMapper) {
        this.clazz = clazz;
        this.rowMapper = rowMapper;
    }

    public static <T> EntityClass<T> of(Class<T> clazz) {
        RowMapper<T> rowMapper = RowMapperFactory.create(clazz, MySQLDialect.getInstance());
        return new EntityClass<>(clazz, rowMapper);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return clazz.getName();
    }

    public RowMapper<T> getRowMapper() {
        return rowMapper;
    }

}
