package persistence.entity;

import jdbc.RowMapper;
import persistence.model.AbstractEntityField;
import persistence.model.PersistentClass;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractEntityRowMapper<T> implements RowMapper<T> {

    protected void setEntityFieldsValue(final PersistentClass<T> persistentClass, final Object entity, final String tableName, final ResultSet resultSet) {
        persistentClass.getColumns().forEach(field -> setEntityFieldValue(entity, resultSet, field, tableName));
    }

    protected void setEntityFieldValue(final Object joinedEntity, final ResultSet resultSet, final AbstractEntityField field, final String tableName) {
        final String columnLabel = toColumnLabel(tableName, field.getColumnName());
        final Object value = extractColumnResult(resultSet, columnLabel);
        field.setValue(joinedEntity, value);
    }

    protected String toColumnLabel(final String tableName, final String columnName) {
        return tableName + "." + columnName;
    }

    protected Object extractColumnResult(final ResultSet resultSet, final String columnLabel) {
        try {
            return resultSet.getObject(columnLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
