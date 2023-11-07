package persistence.entity.mapper;

import persistence.core.EntityIdColumn;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityIdMapper extends EntityColumnsMapper {

    private final EntityIdColumn idColumn;

    private EntityIdMapper(final EntityIdColumn idColumn) {
        this.idColumn = idColumn;
    }

    public static EntityColumnsMapper of(final EntityIdColumn idColumn) {
        return new EntityIdMapper(idColumn);
    }

    @Override
    public <T> void mapColumnsInternal(final ResultSet resultSet, final T instance) throws SQLException {
        final String fieldName = idColumn.getFieldName();
        final String columnName = idColumn.getName();
        final Object object = resultSet.getObject(columnName);
        ReflectionUtils.injectField(instance, fieldName, object);
    }

}
