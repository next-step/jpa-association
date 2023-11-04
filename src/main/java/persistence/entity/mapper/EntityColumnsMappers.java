package persistence.entity.mapper;

import persistence.core.EntityColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityColumnsMappers {

    private final List<EntityColumnsMapper> entityColumnsMappers;

    public EntityColumnsMappers(final EntityColumns columns) {
        this.entityColumnsMappers = List.of(
                new EntityIdMapper(columns.getId()),
                new EntityFieldMapper(columns.getFieldColumns()),
                new EntityOneToManyMapper(columns.getOneToManyColumns())
        );
    }

    public <T> void mapColumns(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityColumnsMapper columnMapper : entityColumnsMappers) {
            columnMapper.mapColumns(resultSet, instance);
        }
    }
}
