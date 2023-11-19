package persistence.entity.persister;

import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.OneToManyAssociation;
import persistence.entity.loader.OneToManyEntityLoader;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class OneToManyEntityPersister extends AbstractEntityPersister {

    private static final Logger log = LoggerFactory.getLogger(OneToManyEntityPersister.class);
    private final OneToManyEntityLoader entityLoader;

    private OneToManyEntityPersister(JdbcTemplate jdbcTemplate,
                                    QueryGenerator queryGenerator,
                                    EntityMeta entityMeta) {
        super(jdbcTemplate, queryGenerator, entityMeta);
        entityLoader = OneToManyEntityLoader.create(entityMeta);
    }

    public static OneToManyEntityPersister create(JdbcTemplate jdbcTemplate,
                                           QueryGenerator queryGenerator,
                                           EntityMeta entityMeta) {
        return new OneToManyEntityPersister(jdbcTemplate, queryGenerator, entityMeta);
    }

    @Override
    public <T> T find(Class<T> tClass, Object id) {
        final String query = queryGenerator
                .select()
                .findByIdOneToManyQuery(id);

        log.info(query);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> entityLoader.load(tClass, resultSet));
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final String query = queryGenerator
                .select()
                .findAllOneToManyQuery();

        log.info(query);

        return jdbcTemplate.queryForAll(query, (resultSet) -> entityLoader.loadAll(tClass, resultSet));
    }

    public <T> List<?> findMany(T instance, OneToManyAssociation oneToManyAssociation) {
        final String query = queryGenerator
                .select()
                .findByForeignerId(entityMeta.getPkValue(instance));

        log.debug(query);

        return jdbcTemplate.query(query, (resultSet)
                -> entityLoader.resultSetToEntity(oneToManyAssociation.getManyAssociationClassType(), resultSet));
    }
}
