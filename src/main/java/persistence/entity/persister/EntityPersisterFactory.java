package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class EntityPersisterFactory {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EntityPersister create(Class clazz, Dialect dialect) {
        EntityMeta entityMeta = EntityMeta.from(clazz);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        return make(entityMeta, queryGenerator);
    }

    private EntityPersister make(EntityMeta entityMeta, QueryGenerator queryGenerator) {
        if (entityMeta.hasLazyOneToMayAssociation()) {
            return OneToManyLazyEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
        }

        if (entityMeta.hasOneToManyAssociation()) {
            return OneToManyEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
        }

        return SimpleEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
    }
}
