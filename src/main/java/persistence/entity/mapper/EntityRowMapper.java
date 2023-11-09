package persistence.entity.mapper;

import persistence.core.EntityColumns;
import persistence.core.EntityMetadata;
import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityRowMapper<T> {
    private final Class<T> clazz;
    private final EntityColumnsMapperChain entityColumnsMapperChain;

    private EntityRowMapper(final Class<T> clazz, final EntityColumns entityColumns) {
        this.clazz = clazz;
        this.entityColumnsMapperChain = EntityColumnsMapperChain.of(entityColumns);
    }

    public static <T> EntityRowMapper<T> of(final EntityMetadata<T> entityMetadata) {
        return new EntityRowMapper<>(entityMetadata.getType(), entityMetadata.getColumns());
    }

    public T mapRow(final ResultSet resultSet) {
        try {
            final T instance = ReflectionUtils.createInstance(clazz);
            entityColumnsMapperChain.mapColumns(resultSet, instance);
            return instance;
        } catch (final SQLException e) {
            throw new PersistenceException("ResultSet Mapping 중 에러가 발생했습니다.", e);
        }
    }

}
