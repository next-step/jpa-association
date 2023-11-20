package persistence.entity.persister;

import java.lang.reflect.Field;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityKey;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public abstract class AbstractEntityPersister implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(AbstractEntityPersister.class);

    protected final JdbcTemplate jdbcTemplate;
    protected final QueryGenerator queryGenerator;
    protected final EntityMeta entityMeta;

    public AbstractEntityPersister(JdbcTemplate jdbcTemplate
            , QueryGenerator queryGenerator
            , EntityMeta entityMeta

    ) {

        this.jdbcTemplate = jdbcTemplate;
        this.queryGenerator = queryGenerator;
        this.entityMeta = entityMeta;

    }

    public <T> T insert(T entity) {
        final String query = queryGenerator.insert().build(entity);

        log.info(query);

        if (entityMeta.isAutoIncrement()) {
            final long id = jdbcTemplate.insertForGenerateKey(query);
            changeValue(entityMeta.getPkColumn(), entity, id);
        }

        return entityMeta.createCopyEntity(entity);
    }

    private void changeValue(EntityColumn column, Object entity, Object value) {
        try {
            final Field field = entity.getClass().getDeclaredField(column.getFieldName());
            field.setAccessible(true);
            field.set(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(Object entity) {
        final String query = queryGenerator.update().build(entity);

        log.info(query);
        jdbcTemplate.execute(query);
        return true;
    }

    public void deleteByKey(EntityKey entityKey) {
        final String query = queryGenerator
                .delete()
                .build(entityKey.getId());

        log.info(query);

        jdbcTemplate.execute(query);
    }
}
