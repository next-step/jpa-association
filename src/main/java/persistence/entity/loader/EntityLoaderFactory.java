package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.EntityLoader;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class EntityLoaderFactory {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoaderFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EntityLoader create(EntityMeta entityMeta, QueryGenerator queryGenerator) {
        if (entityMeta.hasLazyOneToMayAssociation()) {
            return new OneToManyLazyLoader(jdbcTemplate, queryGenerator, new OneToManyLazyMapper(entityMeta));
        }
        if (entityMeta.hasEagerOneToMayAssociation()) {
            return new OneToManyEntityLoader(jdbcTemplate, queryGenerator, new OneToManyEntityMapper(entityMeta));
        }
        return new SimpleEntityLoader(jdbcTemplate, queryGenerator, new SimpleEntityMapper(entityMeta));
    }

}
