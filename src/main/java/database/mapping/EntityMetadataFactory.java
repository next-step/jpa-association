package database.mapping;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EntityMetadataFactory {
    private static final Map<Class<?>, EntityMetadata> metadataMap = new HashMap<>();

    public static EntityMetadata get(Class<?> entityClass) {
        if (metadataMap.get(entityClass) == null) {
            EntityMetadata entityMetadata = EntityMetadata.fromClass(entityClass);
            metadataMap.put(entityClass, entityMetadata);
        }
        return metadataMap.get(entityClass);
    }

    public static EntityMetadata get(Type type) {
        return get((Class) type);
    }
}
