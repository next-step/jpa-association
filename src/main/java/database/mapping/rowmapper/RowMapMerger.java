package database.mapping.rowmapper;

import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: 테스트 추가
public class RowMapMerger<T> {
    private final List<RowMap<T>> rowMaps;
    private final Class<T> clazz;

    public RowMapMerger(List<RowMap<T>> rowMaps, Class<T> clazz) {
        this.rowMaps = rowMaps;
        this.clazz = clazz;
    }

    public Optional<T> merge() {
        if (rowMaps.isEmpty()) {
            return Optional.empty();
        }

        T entity = rowMaps.get(0).getParentEntity();

        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        List<Association> associations = entityMetadata.getAssociations();
        for (Association association : associations) {
            // TODO: 연관관계가 Collection 이 아니면 달라져야 함
            setFieldValue(entity, association.getFieldName(), filterEntitiesByType(association.getEntityType()));
        }

        return Optional.of(entity);
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
