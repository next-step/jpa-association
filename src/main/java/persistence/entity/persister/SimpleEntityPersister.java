package persistence.entity.persister;


import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class SimpleEntityPersister extends AbstractEntityPersister {
    private static final Logger log = LoggerFactory.getLogger(SimpleEntityPersister.class);

    private final EntityLoader entityLoader;

    private SimpleEntityPersister(JdbcTemplate jdbcTemplate
            , QueryGenerator queryGenerator
            , EntityMeta entityMeta
      ) {
        super(jdbcTemplate, queryGenerator, entityMeta);
        entityLoader = SimpleEntityLoader.create();
    }

    public static SimpleEntityPersister create(JdbcTemplate jdbcTemplate
            , QueryGenerator queryGenerator
            , EntityMeta entityMeta) {
        return new SimpleEntityPersister(jdbcTemplate, queryGenerator, entityMeta);
    }

    @Override
    public <T> T find(Class<T> tClass, Object id) {
        final String query = queryGenerator
                .select()
                .findByIdQuery(id);

        log.info(query);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> entityLoader.load(tClass, resultSet));
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final String query = queryGenerator
                .select()
                .findAllQuery();

        return jdbcTemplate.query(query, (resultSet) -> entityLoader.load(tClass, resultSet));
    }
}
