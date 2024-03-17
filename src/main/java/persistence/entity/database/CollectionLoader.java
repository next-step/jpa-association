package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.rowmapper.JoinedRow;
import database.mapping.rowmapper.JoinedRowMapper;
import database.mapping.rowmapper.JoinedRowsCombiner;
import database.sql.dml.CustomSelect;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;

import java.util.List;
import java.util.Optional;

public class CollectionLoader {
    private final EntityLoader entityLoader;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public CollectionLoader(EntityLoader entityLoader, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityLoader = entityLoader;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> Optional<T> load(Class<T> clazz, Long id) {
        String query = new CustomSelect(clazz).buildQuery(WhereMap.of("id", id));
        JoinedRowMapper<T> rowMapper = new JoinedRowMapper<>(clazz, dialect);
        List<JoinedRow<T>> joinedRows = jdbcTemplate.query(query, rowMapper);

        return new JoinedRowsCombiner<>(joinedRows, clazz, getAssociations(clazz), entityLoader).merge();
    }

    // XXX: 뜬금없이 가져온 EntityMetadata
    private static <T> List<Association> getAssociations(Class<T> clazz) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        return entityMetadata.getAssociations();
    }
}
