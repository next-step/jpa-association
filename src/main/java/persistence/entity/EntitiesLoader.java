package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlBuilder;

import java.util.List;
import java.util.Map;

public class EntitiesLoader<T> {
    private final Class<T> clazz;
    private final EntityMeta meta;
    private final JdbcTemplate jdbcTemplate;
    private final DmlBuilder dml;

    public EntitiesLoader(Class<T> clazz, JdbcTemplate jdbcTemplate, DmlBuilder dml) {
        this.clazz = clazz;
        this.meta = new EntityMeta(clazz);
        this.jdbcTemplate = jdbcTemplate;
        this.dml = dml;
    }

    public List<T> findAll() {
        if (meta.isEagerOneToMany()) {
            EagerOneToManyEntityLoader<T> loader = new EagerOneToManyEntityLoader<>(clazz);
            jdbcTemplate.query(
                    dml.getEagerJoinQuery(meta),
                    loader
            );
            return loader.collectDistinct();
        }
        if (meta.isLazyOneToMany()) {
            EntitiesLoader childrenLoader = new EntitiesLoader(meta.getChildClass(), jdbcTemplate, dml);
            return jdbcTemplate.query(
                    dml.getFindAllQuery(clazz),
                    new LazyOneToManyEntityLoader<>(clazz, childrenLoader)
            );
        }
        return jdbcTemplate.query(
                dml.getFindAllQuery(clazz),
                new EntityLoader<>(clazz)
        );
    }

    public List<T> findAllBy(Map<String, Object> condition) {
        if (meta.isLazyOneToMany()) {
            EntitiesLoader childrenLoader = new EntitiesLoader(meta.getChildClass(), jdbcTemplate, dml);
            return jdbcTemplate.query(
                    dml.getFindAllQuery(clazz) + dml.getWhereQuery(condition),
                    new LazyOneToManyEntityLoader<>(clazz, childrenLoader)
            );
        }
        return jdbcTemplate.query(
                dml.getFindAllQuery(clazz) + dml.getWhereQuery(condition),
                new EntityLoader<>(clazz)
        );
    }
}
