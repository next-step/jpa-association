package persistence.entity;

import jdbc.RowMapper;
import persistence.model.PersistentClass;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityRowMapper<T> implements RowMapper<T> {
    private final PersistentClass<T> persistentClass;

    public EntityRowMapper(final PersistentClass<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    @Override
    public T mapRow(final ResultSet resultSet) throws SQLException {
        return mapToEntity(persistentClass, resultSet);
    }

    private T mapToEntity(final PersistentClass<T> persistentClass, final ResultSet resultSet) {
        final T instance = this.persistentClass.createInstance();
        setEntityFieldsValue(instance, persistentClass.getTableName(), resultSet);

        return instance;
    }

    private void setEntityFieldsValue(final Object entity, final String tableName, final ResultSet resultSet) {
        this.persistentClass.getColumns().forEach(field -> {
            final Object value = extractColumnResult(resultSet, toColumnLabel(tableName, field.getColumnName()));
            field.setValue(entity, value);
        });
    }

    private String toColumnLabel(final String tableName, final String columnName) {
        return tableName + "." + columnName;
    }

    private Object extractColumnResult(final ResultSet resultSet, final String columnLabel) {
        try {
            return resultSet.getObject(columnLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
