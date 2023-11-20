package persistence.entity.persister;

import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.loader.OneToManyLazyLoader;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class OneToManyLazyEntityPersister extends AbstractEntityPersister {
    private static final Logger log = LoggerFactory.getLogger(OneToManyLazyEntityPersister.class);
    private final OneToManyLazyLoader entityLoader;

    private OneToManyLazyEntityPersister(JdbcTemplate jdbcTemplate
            , QueryGenerator queryGenerator
            , EntityMeta entityMeta
    ) {
        super(jdbcTemplate, queryGenerator, entityMeta);
        entityLoader = OneToManyLazyLoader.create(entityMeta,
                OneToManyEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta));
    }

    public static OneToManyLazyEntityPersister create(JdbcTemplate jdbcTemplate
            , QueryGenerator queryGenerator
            , EntityMeta entityMeta) {
        return new OneToManyLazyEntityPersister(jdbcTemplate, queryGenerator, entityMeta);
    }

    @Override
    public <T> T find(Class<T> tClass, Object id) {
        final String query = queryGenerator
                .select()
                .findByIdQuery(id);

        log.debug(query);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> entityLoader.load(tClass, resultSet));
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final String query = queryGenerator
                .select()
                .findAllQuery();

        log.debug(query);

        return jdbcTemplate.query(query,
                (resultSet) -> entityLoader.load(tClass, resultSet));
    }
}
