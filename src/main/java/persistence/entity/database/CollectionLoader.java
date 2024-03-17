package persistence.entity.database;

import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.rowmapper.MultiRowMapper;
import database.mapping.rowmapper.RowMap;
import database.mapping.rowmapper.RowMapMerger;
import database.sql.dml.CustomSelect;
import jdbc.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// XXX: CustomSelect 를 사용하는 것 외에 Collection 이란 이름을 붙이는 것이 어색함
// XXX: CollectionLoader 의 목적을 명확히
public class CollectionLoader {
    private final JdbcTemplate jdbcTemplate;
    private Dialect dialect;

    public CollectionLoader(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> Optional<T> load(Class<T> clazz, Long id) {
        String query = new CustomSelect(clazz).buildQuery(Map.of("id", id));
        MultiRowMapper<T> rowMapper = new MultiRowMapper<>(clazz, MySQLDialect.getInstance());
        List<RowMap<T>> rowMaps = jdbcTemplate.query(query, rowMapper);

        return new RowMapMerger<>(rowMaps, clazz, getAssociations(clazz), jdbcTemplate, dialect).merge();
    }

    // XXX: 뜬금없이 가져온 EntityMetadata
    private static <T> List<Association> getAssociations(Class<T> clazz) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        return entityMetadata.getAssociations();
    }
}
