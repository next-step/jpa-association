package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.Entity;
import persistence.sql.Id;
import persistence.sql.dml.DmlQueryBuilder;

public class MyEntityPersister implements EntityPersister {
    private final Map<Long, Entity> entitySnapshotsByKey = new ConcurrentHashMap<>();
    private final Map<Class<?> , RowMapper<?>> rowMappers;
    private final JdbcTemplate jdbcTemplate;

    public MyEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        final HashMap<Class<?>, RowMapper<?>> rowMappers = new HashMap<>();
        rowMappers.put(Person.class, new PersonRowMapper());
        this.rowMappers = rowMappers;
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        final DmlQueryBuilder<?> queryBuilder = new DmlQueryBuilder<>(entity.getClass());
        final String sql = queryBuilder.findById(id);
        Object instance = jdbcTemplate.queryForObject(sql, rowMappers.get(entity.getClass()));
        if (instance == null) {
            return null;
        }
        entitySnapshotsByKey.put(id, new Entity(instance));
        return instance;
    }

    @Override
    public Object getCachedDatabaseSnapshot(Long id) {
        Entity entity = entitySnapshotsByKey.get(id);
        if (entity == null) {
            return null;
        }
        return entity.getEntity();
    }

    @Override
    public Object load(Class<?> clazz, Long id) {
        Object instance = getCachedDatabaseSnapshot(id);
        if (instance != null) {
            return instance;
        }
        try {
            instance = getDatabaseSnapshot(id, clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        if (instance == null) {
            throw new IllegalArgumentException("ObjectNotFoundException");
        }
        return instance;
    }

    @Override
    public void insert(Object entity) {
        final Class<?> clazz = entity.getClass();
        final DmlQueryBuilder<?> dmlQueryBuilder = new DmlQueryBuilder<>(clazz);
        final String sql = dmlQueryBuilder.insert(entity);

        Object key = jdbcTemplate.executeAndReturnKey(sql);
        entitySnapshotsByKey.put((Long) key, new Entity(entity));
    }

    @Override
    public void update(Object entity) {
        Entity snapshot = entitySnapshotsByKey.get((Long) new Id(entity).getValue());
        if (snapshot == null) {
            final Class<?> clazz = entity.getClass();
            final DmlQueryBuilder<?> dmlQueryBuilder = new DmlQueryBuilder<>(clazz);
            final String sql = dmlQueryBuilder.update(entity);
            Object key = jdbcTemplate.executeAndReturnKey(sql);
            entitySnapshotsByKey.put((Long) key, new Entity(entity));
        }

        Object snapshotEntity = Objects.requireNonNull(snapshot).getEntity();

        Map<Field, Object> valuesByColumnName = new HashMap<>();
        for (Entry<String, Field> entry : new Entity(entity).getColumns().entrySet()) {
            Field field = entry.getValue();
            field.setAccessible(true);
            Object entityValue;
            try {
                entityValue = field.get(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Object snapshotEntityValue;
            try {
                snapshotEntityValue = field.get(snapshotEntity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (entityValue != null && !entityValue.equals(snapshotEntityValue)) {
                valuesByColumnName.put(field, entityValue);
            }
        }
        if (!valuesByColumnName.isEmpty()) {
            final DmlQueryBuilder<?> dmlQueryBuilder = new DmlQueryBuilder<>(entity.getClass());
            final String sql = dmlQueryBuilder.update(entity, valuesByColumnName);
            System.out.println("debugging: \n" + sql);
            jdbcTemplate.execute(sql);
        }
    }

    @Override
    public void delete(Object entity) {
        entitySnapshotsByKey.remove((Long) new Id(entity).getValue());
        final String deleteSql = new DmlQueryBuilder<>(entity.getClass()).delete(entity);
        jdbcTemplate.execute(deleteSql);
    }
}
