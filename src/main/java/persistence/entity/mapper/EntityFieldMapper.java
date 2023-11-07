package persistence.entity.mapper;

import persistence.core.EntityColumn;
import persistence.core.EntityFieldColumn;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityFieldMapper extends EntityColumnsMapper {

    private final List<EntityFieldColumn> columns;

    private EntityFieldMapper(final List<EntityFieldColumn> fieldColumns) {
        this.columns = fieldColumns;
    }

    public static EntityColumnsMapper of(final List<EntityFieldColumn> fieldColumns) {
        return new EntityFieldMapper(fieldColumns);
    }

    @Override
    public <T> void mapColumnsInternal(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityColumn column : columns) {
            final String fieldName = column.getFieldName();
            final String columnName = column.getName();
            final Object object = resultSet.getObject(columnName);
            ReflectionUtils.injectField(instance, fieldName, object);
        }
    }

}
