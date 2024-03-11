package database.mapping;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EntityMetadataFactory {
    private static final Map<Class<?>, EntityMetadata> metadataMap = new HashMap<>();

    public static EntityMetadata get(Class<?> entityClass) {
        if (metadataMap.get(entityClass) == null)
            metadataMap.put(entityClass, EntityMetadata.fromClass(entityClass));
        return metadataMap.get(entityClass);
    }

    public static EntityMetadata get(Type type) {
        return get((Class) type);
    }
}
