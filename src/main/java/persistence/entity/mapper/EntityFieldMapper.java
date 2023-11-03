package persistence.entity.mapper;

import persistence.core.EntityColumn;
import persistence.core.EntityFieldColumn;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityFieldMapper implements EntityColumnsMapper {

    private final List<EntityFieldColumn> columns;

    public EntityFieldMapper(final List<EntityFieldColumn> columns) {
        this.columns = columns;
    }

    public <T> void mapColumns(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityColumn column : columns) {
            final String fieldName = column.getFieldName();
            final String columnName = column.getName();
            final Object object = resultSet.getObject(columnName);
            ReflectionUtils.injectField(instance, fieldName, object);
        }
    }

}
