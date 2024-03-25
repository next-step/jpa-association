package persistence.model;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersistentClassMapping {

    private static final Map<String, PersistentClass<?>> persistentClassMap = new HashMap<>();
    private static final CollectionPersistentClassBinder collectionPersistentClassBinder = new CollectionPersistentClassBinder();
    private static final EntityJoinFieldMapping[] joinFieldMappings = initJoinFieldMappings();

    private static EntityJoinFieldMapping[] initJoinFieldMappings() {
        return new EntityJoinFieldMapping[]{
                new EntityManyToManyFieldMapping(),
                new EntityManyToOneFieldMapping(),
                new EntityOneToManyFieldMapping(),
                new EntityOneToOneFieldMapping()
        };
    }

    public static <T> void putPersistentClass(final Class<T> entityClass) {
        final PersistentClass<T> persistentClass = createPersistentClass(entityClass);
        persistentClassMap.putIfAbsent(persistentClass.getEntityName(), persistentClass);
        extractEntityFields(entityClass, persistentClass);
    }

    private static <T> PersistentClass<T> createPersistentClass(final Class<T> entityClass) {
        return new PersistentClass<T>(entityClass);
    }

    private static <T> void extractEntityFields(final Class<?> entityClass, final PersistentClass<T> persistentClass) {
        Arrays.stream(entityClass.getDeclaredFields())
                .filter(PersistentClassMapping::isColumnField)
                .forEach(field -> {
                    final AbstractEntityField entityField = AbstractEntityField.createEntityField(field);
                    persistentClass.addEntityField(entityField);

                    if (entityField.isJoinField()) {
                        // TODO 일급 컬렉션으로 리팩토링
                        Arrays.stream(joinFieldMappings).filter(mapping -> mapping.support(field))
                                .findFirst()
                                .ifPresent(mapping -> {
                                    final Class<?> entityType = mapping.getEntityType(field);
                                    final PersistentClass<?> joinedPersistentClass = getPersistentClass(entityType.getName(), entityType);
                                    final CollectionPersistentClass collectionPersistentClass =  mapping.createCollectionPersistentClass(persistentClass, joinedPersistentClass, field);
                                    collectionPersistentClassBinder.addClass(collectionPersistentClass);
                                });
                    }
                });
    }

    private static boolean isColumnField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private static PersistentClass<?> getPersistentClass(final String entityName, final Class<?> entityClass) {
        return persistentClassMap.getOrDefault(entityName, createPersistentClass(entityClass));
    }

    public static <T> PersistentClass<T> getPersistentClass(final Class<T> clazz) {
        return (PersistentClass<T>) getPersistentClass(clazz.getName());
    }

    public static PersistentClass<?> getPersistentClass(final String entityName) {
        final PersistentClass<?> persistentClass = persistentClassMap.get(entityName);

        if (Objects.isNull(persistentClass)) {
            throw new MetaDataModelMappingException("entity meta data is not initialized : " + entityName);
        }

        return persistentClass;
    }

}
