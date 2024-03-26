package persistence.model;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class PersistentClassMapping {

    private static final Map<String, PersistentClass<?>> persistentClassMap = new HashMap<>();
    private static final CollectionPersistentClassBinder collectionPersistentClassBinder = new CollectionPersistentClassBinder();
    private static final EntityJoinFieldMapper entityJoinFieldMapper = new EntityJoinFieldMapper();

    public static <T> void putPersistentClass(final Class<T> entityClass) {
        final PersistentClass<T> persistentClass = createPersistentClass(entityClass);
        persistentClassMap.putIfAbsent(persistentClass.getEntityName(), persistentClass);
    }

    private static <T> PersistentClass<T> createPersistentClass(final Class<T> entityClass) {
        final PersistentClass<T> persistentClass = new PersistentClass<>(entityClass);
        final List<AbstractEntityField> entityFields = extractEntityFields(entityClass);
        persistentClass.addEntityFields(entityFields);

        return persistentClass;
    }

    private static <T> List<AbstractEntityField> extractEntityFields(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields()).filter(PersistentClassMapping::isColumnField).map(AbstractEntityField::createEntityField).collect(Collectors.toList());
    }

    private static boolean isColumnField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private static <T> void createCollectionPersistentClass(final PersistentClass<T> persistentClass, final AbstractEntityField entityField) {
        final Field field = entityField.getField();
        final EntityJoinField entityJoinField = (EntityJoinField) entityField;

        final EntityJoinFieldMapping joinFieldMapping = entityJoinFieldMapper.findJoinFieldMapping(field);

        final boolean lazy = joinFieldMapping.isLazy(field);
        entityJoinField.setLazy(lazy);

        final Class<?> entityType = joinFieldMapping.getEntityType(field);
        final PersistentClass<?> joinedPersistentClass = getPersistentClass(entityType.getName());
        final CollectionPersistentClass collectionPersistentClass =
                collectionPersistentClassBinder.getCollectionPersistentClassOrDefault(entityType.getName(), joinFieldMapping.createCollectionPersistentClass(joinedPersistentClass));
        collectionPersistentClass.addAssociation(persistentClass, lazy);
    }

    public static void setCollectionPersistentClassBinder() {
        persistentClassMap.values().forEach(persistentClass -> {
            final List<EntityJoinField> joinFields = persistentClass.getJoinFields();
            joinFields.forEach(field -> createCollectionPersistentClass(persistentClass, field));
        });
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

    public static CollectionPersistentClassBinder getCollectionPersistentClassBinder() {
        return collectionPersistentClassBinder;
    }

}
