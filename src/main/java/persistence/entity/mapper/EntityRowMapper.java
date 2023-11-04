package persistence.entity.mapper;

import persistence.core.*;
import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityRowMapper<T> {
    private final Class<T> clazz;
    private final EntityColumnsMapper entityColumnsMapper;

    public EntityRowMapper(final Class<T> clazz) {
        final EntityColumns columns = EntityMetadataProvider.getInstance().getEntityMetadata(clazz).getColumns();
        this.clazz = clazz;
        this.entityColumnsMapper = EntityColumnsMappers.of(columns);
    }

    public T mapRow(final ResultSet resultSet) {
        try {
            final T instance = ReflectionUtils.createInstance(clazz);
            entityColumnsMapper.mapColumns(resultSet, instance);
            return instance;
        } catch (final SQLException e) {
            throw new PersistenceException("ResultSet Mapping 중 에러가 발생했습니다.", e);
        }
    }

}
