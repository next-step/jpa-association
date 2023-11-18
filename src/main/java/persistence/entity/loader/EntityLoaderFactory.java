package persistence.entity.loader;

import jdbc.JdbcTemplate;
import jdbc.mapper.SingleEntityMapper;
import jdbc.mapper.OneToManyEntityMapper;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class EntityLoaderFactory {
    private final JdbcTemplate jdbcTemplate;

    private final EntityMetadata entityMetadata;

    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityLoaderFactory(JdbcTemplate jdbcTemplate, EntityMetadata entityMetadata, DmlQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetadata = entityMetadata;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public EntityLoader<?> create(Class<?> clazz) {
        if(entityMetadata.hasAssociation()) {
            return new OneToManyEagerEntityLoader<>(jdbcTemplate, entityMetadata, dmlQueryBuilder, new OneToManyEntityMapper<>(clazz));
        }

        return new SingleEntityLoader<>(jdbcTemplate, entityMetadata, dmlQueryBuilder, new SingleEntityMapper<>(clazz));
    }
}
