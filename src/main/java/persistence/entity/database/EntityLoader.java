package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.rowmapper.SingleRowMapperFactory;
import database.sql.dml.Select;
import database.sql.dml.SelectByPrimaryKey;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> Optional<T> load(Class<T> clazz, Long id) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new SelectByPrimaryKey(
                entityMetadata.getTableName(),
                entityMetadata.getAllFieldNames()
        ).byId(id).buildQuery();
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }

    public <T> List<T> load(Class<T> clazz, WhereMap whereMap) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new Select(entityMetadata.getTableName(), entityMetadata.getAllFieldNames())
                .where(whereMap)
                .buildQuery();
        return jdbcTemplate.query(query, rowMapper);
    }
}
