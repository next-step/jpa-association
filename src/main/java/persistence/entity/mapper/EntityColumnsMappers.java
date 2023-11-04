package persistence.entity.mapper;

import persistence.core.EntityColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityColumnsMappers implements EntityColumnsMapper {

    private final List<EntityColumnsMapper> entityColumnsMappers;

    private EntityColumnsMappers(final EntityColumns columns) {
        this.entityColumnsMappers = List.of(
                EntityIdMapper.of(columns.getId()),
                EntityFieldMapper.of(columns.getFieldColumns()),
                EntityOneToManyMapper.of(columns.getOneToManyColumns())
        );
    }

    public static EntityColumnsMapper of(final EntityColumns columns) {
        return new EntityColumnsMappers(columns);
    }

    @Override
    public <T> void mapColumns(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityColumnsMapper columnMapper : entityColumnsMappers) {
            columnMapper.mapColumns(resultSet, instance);
        }
    }
}
