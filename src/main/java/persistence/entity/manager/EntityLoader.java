package persistence.entity.manager;

import jakarta.persistence.OneToMany;
import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T load(EntityMeta entityMeta, Long id) {
        Class<T> entityClass = (Class<T>) entityMeta.getEntityClass();

        if (entityMeta.isEagerLoading()) {
            String selectQuery = selectQueryBuilder.findByIdWithJoin(entityMeta, id);
            return jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(entityClass));
        }

        if (entityMeta.isLazyLoading()) {
            String selectQuery = selectQueryBuilder.findById(entityMeta, id);
            T entity = jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<T>(entityClass));

            Field[] declaredFields = entityClass.getDeclaredFields();
            Arrays.stream(declaredFields)
                    .filter(field -> field.isAnnotationPresent(OneToMany.class))
                    .forEach(field -> setProxy(field, entity, entityMeta));
            return entity;
        }

        String selectQuery = selectQueryBuilder.findById(entityMeta, id);
        return jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(entityClass));
    }

    private void setProxy(Field field, Object entity, EntityMeta parentEntityMeta) {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(parentEntityMeta.getOneToManyColumnClass());
        try {
            field.setAccessible(true);
            Long foreignKey = (Long) parentEntityMeta.getIdColumn().getField().get(entity);
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(field.getType());
            enhancer.setCallback((LazyLoader) () -> {
                Class<Object> entityClass = (Class<Object>) entityMeta.getEntityClass();
                String foreignKeyName = parentEntityMeta.getForeignKeyName();
                String selectQuery = selectQueryBuilder.findAllByForeignKey(entityMeta, foreignKeyName, foreignKey);
                return jdbcTemplate.query(selectQuery, new RowMapperImpl<>(entityClass));
            });
            field.set(entity, enhancer.create());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
