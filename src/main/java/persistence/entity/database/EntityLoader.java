package persistence.entity.database;

import database.dialect.MySQLDialect;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.column.EntityColumn;
import database.mapping.rowmapper.SingleRowMapperFactory;
import database.sql.dml.Select;
import database.sql.dml.SelectByPrimaryKey;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final MySQLDialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, MySQLDialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> List<T> load(Class<T> clazz, Collection<Long> ids) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        List<EntityColumn> allEntityColumns = entityMetadata.getAllEntityColumns();
        String query = new Select(entityMetadata.getTableName(), allEntityColumns)
                .where(Map.of("id", ids))
                .buildQuery();
        return jdbcTemplate.query(query, rowMapper);
    }

    public <T> Optional<T> load(Class<T> clazz, Long id) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new SelectByPrimaryKey(
                entityMetadata.getTableName(),
                entityMetadata.getAllEntityColumns()
        ).byId(id).buildQuery();
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }
}
