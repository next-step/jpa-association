package persistence.entity.mapper;

import persistence.core.EntityColumns;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityColumnsMapperChain {

    private final EntityColumnsMapper mapper;

    private EntityColumnsMapperChain(final EntityColumns columns) {
        this.mapper = EntityIdMapper.of(columns.getId())
                .next(EntityFieldMapper.of(columns.getFieldColumns()))
                .next(EntityOneToManyMapper.of(columns.getEagerOneToManyColumns()));
    }

    public static EntityColumnsMapperChain of(final EntityColumns columns) {
        return new EntityColumnsMapperChain(columns);
    }

    public <T> void mapColumns(final ResultSet resultSet, final T instance) throws SQLException {
        mapper.mapColumns(resultSet, instance);
    }
}
