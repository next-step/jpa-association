package persistence.entity.database;

import database.dialect.MySQLDialect;
import database.mapping.rowmapper.MultiRowMapper;
import database.mapping.rowmapper.RowMap;
import database.mapping.rowmapper.RowMapMerger;
import database.sql.dml.CustomSelect;
import jdbc.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CollectionLoader {
    private final JdbcTemplate jdbcTemplate;

    public CollectionLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> Optional<T> load(Class<T> clazz, Long id) {
        String query = new CustomSelect(clazz).buildQuery(Map.of("id", id));
        MultiRowMapper<T> rowMapper = new MultiRowMapper<>(clazz, MySQLDialect.getInstance());
        List<RowMap<T>> rowMaps = jdbcTemplate.query(query, rowMapper);

        return new RowMapMerger<>(rowMaps, clazz).merge();
    }
}
