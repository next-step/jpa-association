package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.Association;
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

        // TODO: 조만간 clazz 대신 가져올 객체 통해서 얻을 수 있을거라고 믿고 여기 둠
        List<Association> associations = getAssociations(clazz);

        return new JoinedRowsCombiner<>(joinedRows, clazz, associations, entityLoader).merge();
    }

    private static <T> List<Association> getAssociations(Class<T> clazz) {
        return EntityMetadataFactory.get(clazz).getAssociations();
    }

    // XXX: 뜬금없이 가져온 EntityMetadata
}
