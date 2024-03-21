package persistence.entity;

import jakarta.persistence.Entity;
import jdbc.RowMapper;
import persistence.sql.QueryException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class EntityRowMapper<T> implements RowMapper<T> {
    private final PersistentClass<T> persistentClass;

    public EntityRowMapper(Class<T> clazz) {
        final boolean isEntity = clazz.isAnnotationPresent(Entity.class);
        if (!isEntity) {
            throw new QueryException(clazz.getName() + " is not entity");
        }
        this.persistentClass = new PersistentClass<>(clazz);
    }

    @Override
    public T mapRow(final ResultSet resultSet) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        this.persistentClass.initPersistentClass();

        for (int i = 1; i <= columnCount; i++) {
            final String tableName = metaData.getTableName(i);
            final String columnLabel = metaData.getColumnLabel(i);
            final Object value = resultSet.getObject(i);

            persistentClass.setFieldValue(tableName, columnLabel, value);
        }

        return persistentClass.getEntity();
    }

}
