package database.mapping.rowmapper;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.database.EntityLoader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: 테스트 추가
public class RowMapMerger<T> {
    private final List<RowMap<T>> rowMaps;
    private final Class<T> clazz;
    private final List<Association> associations;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public RowMapMerger(List<RowMap<T>> rowMaps, Class<T> clazz, List<Association> associations,
                        JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.rowMaps = rowMaps;
        this.clazz = clazz;
        this.associations = associations;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public Optional<T> merge() {
        if (rowMaps.isEmpty()) {
            return Optional.empty();
        }

        T entity = rowMaps.get(0).getParentEntity();

        for (Association association : this.associations) {
            if (association.isLazyLoad()) {
                setFieldValue(entity, association.getFieldName(), lazyLoadProxy(association));
            } else {
                // TODO: 연관관계가 Collection 이 아니면 달라져야 함
                setFieldValue(entity, association.getFieldName(), filterEntitiesByType(association.getEntityType()));
            }
        }

        return Optional.of(entity);
    }

    private Object lazyLoadProxy(Association association) {
        Class<?> entityType = association.getAssociationType(); // XXX 코드정리
        Class<?> entityType1 = association.getEntityType();

        return Enhancer.create(entityType, (LazyLoader) () -> {
            EntityLoader entityLoader = new EntityLoader(jdbcTemplate, dialect);
            return entityLoader.load(entityType1, Map.of(association.getForeignKeyColumnName(), 1L));
        });
    }

    private <R> void setFieldValue(R entity, String fieldName, Object value) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        try {
            Field field = entityMetadata.getFieldByFieldName(fieldName);
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <R> List<R> filterEntitiesByType(Class<R> associatedType) {
        return rowMaps.stream().map(rowMap -> rowMap.mapValues(associatedType)).collect(Collectors.toList());
    }
}
