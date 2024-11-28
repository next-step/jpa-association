package persistence.entity.loader;

import jakarta.persistence.FetchType;
import java.lang.reflect.Field;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.loader.proxy.CollectionEntityProxy;
import persistence.meta.ColumnMeta;
import persistence.meta.RelationMeta;
import persistence.meta.SchemaMeta;
import persistence.meta.store.ClassSchemaMetas;
import persistence.sql.dml.query.builder.SelectQueryBuilder;

public class DefaultEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityCollectionLoader collectionLoader;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.collectionLoader = new EntityCollectionLoader(jdbcTemplate);
    }

    @Override
    public <T> T load(Class<T> clazz, Object id) {
        SchemaMeta schemaMeta = ClassSchemaMetas.get(clazz);
        String query = SelectQueryBuilder.builder(schemaMeta, id).build();

        T instance = jdbcTemplate.queryForObject(query, new EntityRowMapper<>(clazz));
        if (schemaMeta.hasNotRelation()) {
            return instance;
        }

        List<ColumnMeta> columnMetasWithRelation = schemaMeta.columnMetasHasRelation();
        for (ColumnMeta columnMeta : columnMetasWithRelation) {
            load(instance, id, columnMeta);
        }

        return clazz.cast(instance);
    }

    private <T> void load(T instance, Object id, ColumnMeta columnMeta) {
        RelationMeta relationMeta = columnMeta.relationMeta();
        if (FetchType.EAGER == relationMeta.fetchType()) {
            List<?> children = eagerLoad(columnMeta, id);
            mapChildrenField(instance, columnMeta, children);
        }

        if (FetchType.LAZY == relationMeta.fetchType()) {
            List<?> children = lazyLoad(columnMeta, id);
            mapChildrenField(instance, columnMeta, children);
        }
    }

    private List<?> eagerLoad(ColumnMeta columnMeta, Object id) {
        return collectionLoader.load(columnMeta.relationMeta().joinColumnType(), columnMeta.field(), id);
    }

    private List<?> lazyLoad(ColumnMeta columnMeta, Object id) {
        return CollectionEntityProxy.createProxy(columnMeta, id, collectionLoader);
    }

    private void mapChildrenField(Object instance, ColumnMeta columnMeta, List<?> children) {
        try {
            Field field = columnMeta.field();
            field.setAccessible(true);
            field.set(instance, children);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
