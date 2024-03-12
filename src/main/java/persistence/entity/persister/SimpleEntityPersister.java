package persistence.entity.persister;

import java.util.Collection;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.meta.Table;

public class SimpleEntityPersister implements EntityPersister {

    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;

    private SimpleEntityPersister(JdbcTemplate jdbcTemplate) {
        this.dmlGenerator = DmlGenerator.getInstance();
        this.jdbcTemplate = jdbcTemplate;
    }

    public static SimpleEntityPersister from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityPersister(jdbcTemplate);
    }

    @Override
    public boolean update(Object entity) {
        return jdbcTemplate.executeUpdate(dmlGenerator.generateUpdateQuery(entity)) > 0;
    }

    @Override
    public void insert(Object entity) {
        Object id = jdbcTemplate.executeInsert(dmlGenerator.generateInsertQuery(entity));
        Table table = Table.getInstance(entity.getClass());
        table.setIdValue(entity, id);
        insertRelation(table, entity);
    }

    @Override
    public void delete(Object entity) {
        jdbcTemplate.executeUpdate(dmlGenerator.generateDeleteQuery(entity));
    }

    private void insertRelation(Table root, Object entity) {

        List<Object> relatedEntities = root.getRelationValues(entity);
        relatedEntities.stream()
            .map(relatedEntity -> ((Collection<Object>) relatedEntity))
            .flatMap(Collection::stream)
            .forEach(relatedEntity -> {
                Object subId = jdbcTemplate.executeInsert(dmlGenerator.generateInsertQuery(relatedEntity, entity));
                Table table = Table.getInstance(relatedEntity.getClass());
                table.setIdValue(relatedEntity, subId);
            });
    }
}
