package persistence.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class EntityColumnsMapper {

    private EntityColumnsMapper nextMapper;

    public EntityColumnsMapper next(final EntityColumnsMapper mapper) {
        if (Objects.isNull(this.nextMapper)) {
            this.nextMapper = mapper;
        } else {
            this.nextMapper.next(mapper);
        }
        return this;
    }

    public <T> void mapColumns(final ResultSet resultSet, final T instance) throws SQLException {
        mapColumnsInternal(resultSet, instance);
        if (Objects.nonNull(nextMapper)) {
            nextMapper.mapColumns(resultSet, instance);
        }
    }

    protected abstract <T> void mapColumnsInternal(final ResultSet resultSet, T instance) throws SQLException;
}
