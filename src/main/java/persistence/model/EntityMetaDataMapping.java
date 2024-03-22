package persistence.model;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityMetaDataMapping {

    private static final Map<String, EntityMetaData> entityMetaDataMap = new HashMap<>();
    private static final EntityJoinFieldMapping[] joinFieldMappings = initJoinFieldMappings();

    private static EntityJoinFieldMapping[] initJoinFieldMappings() {
        return new EntityJoinFieldMapping[]{
                new EntityManyToManyFieldMapping(),
                new EntityManyToOneFieldMapping(),
                new EntityOneToManyFieldMapping(),
                new EntityOneToOneFieldMapping()
        };
    }

    public static void putMetaData(final Class<?> entityClass) {
        final EntityMetaData metaData = createMetaData(entityClass);
        entityMetaDataMap.putIfAbsent(metaData.getEntityName(), metaData);
    }

    private static EntityMetaData createMetaData(final Class<?> entityClass) {
        final EntityMetaData metaData = new EntityMetaData(entityClass);
        extractEntityFields(entityClass, metaData);
        return metaData;
    }

    private static void extractEntityFields(final Class<?> entityClass, final EntityMetaData metaData) {
        Arrays.stream(entityClass.getDeclaredFields())
                .filter(EntityMetaDataMapping::isColumnField)
                .forEach(field -> Arrays.stream(joinFieldMappings)
                        .filter(mapping -> mapping.support(field))
                        .findFirst()
                        .ifPresentOrElse(
                                mapping -> {
                                    final Class<?> joinedEntityType = mapping.getEntityType(field);
                                    metaData.addEntityJoinEntityField(mapping.create(joinedEntityType, field));
                                },
                                () -> metaData.addEntityField(new EntityField(field.getName(), field.getClass(), field))
                        ));
    }

    private static boolean isColumnField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    public static EntityMetaData getMetaData(final String entityName) {
        final EntityMetaData entityMetaData = entityMetaDataMap.get(entityName);
        if (Objects.isNull(entityMetaData)) {
            throw new MetaDataModelMappingException("entity meta data is not initialized : " + entityName);
        }

        return entityMetaData;
    }

}
