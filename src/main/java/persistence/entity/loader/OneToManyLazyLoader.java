package persistence.entity.loader;

import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityLoader;
import persistence.sql.QueryGenerator;

public class OneToManyLazyLoader implements EntityLoader {
    private final Logger log = LoggerFactory.getLogger(OneToManyLazyLoader.class);
    private final JdbcTemplate jdbcTemplate;
    private final QueryGenerator queryGenerator;
    private final OneToManyLazyMapper entityMapper;

    public OneToManyLazyLoader(JdbcTemplate jdbcTemplate, QueryGenerator queryGenerator,
                               OneToManyLazyMapper oneToManyLazyMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryGenerator = queryGenerator;
        this.entityMapper = oneToManyLazyMapper;
    }

    public <T> T find(Class<T> tClass, Object id) {
        final String query = queryGenerator
                .select()
                .findByIdQuery(id);

        log.debug(query);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> entityMapper.findLazyMapper(tClass, resultSet));
    }

    public <T> List<T> findAll(Class<T> tClass) {
        final String query = queryGenerator.select().findAllQuery();

        log.debug(query);

        return jdbcTemplate.query(query,
                (resultSet) -> entityMapper.findLazyMapper(tClass, resultSet));
    }


}
