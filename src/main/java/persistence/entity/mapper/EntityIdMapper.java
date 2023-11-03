package persistence.entity.mapper;

import persistence.core.EntityIdColumn;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityIdMapper implements EntityColumnsMapper {

    private final EntityIdColumn idColumn;

    public EntityIdMapper(final EntityIdColumn idColumn) {
        this.idColumn = idColumn;
    }

    public void mapColumns(final ResultSet resultSet, final Object instance) throws SQLException {
        final String fieldName = idColumn.getFieldName();
        final String columnName = idColumn.getName();
        final Object object = resultSet.getObject(columnName);
        ReflectionUtils.injectField(instance, fieldName, object);
    }

}
