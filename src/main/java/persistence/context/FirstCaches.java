package persistence.context;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.OneToManyField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FirstCaches {
    private final Map<Class<?>, Map<String, Object>> firstCaches = new HashMap<>();

    public void putFirstCache(Object instance, String instanceId) {
        if (instance == null) {
            return;
        }
        firstCaches.computeIfAbsent(instance.getClass(), k -> new HashMap<>()).put(instanceId, instance);
    }

    public void putFirstCache(Object instance, String instanceId, EntityAttribute entityAttribute) {
        if (instance == null) {
            return;
        }

        firstCaches.computeIfAbsent(instance.getClass(), k -> new HashMap<>()).put(instanceId, instance);

        for (OneToManyField oneToManyField : entityAttribute.getOneToManyFields()) {
            try {
                oneToManyField.getField().setAccessible(true);
                List<?> oneToManyList = (List<?>) oneToManyField.getField().get(instance);
                if (!oneToManyList.isEmpty()) {
                    Object oneToManyInstance = oneToManyList.get(0);
                    String oneToManyInstanceId = getInstanceIdAsString(oneToManyInstance,
                            oneToManyField.getEntityAttribute().getIdAttribute().getField());
                    this.putFirstCache(oneToManyInstance, oneToManyInstanceId, oneToManyField.getEntityAttribute());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        firstCaches.computeIfAbsent(instance.getClass(), k -> new HashMap<>()).put(instanceId, instance);
    }

    public Object getFirstCacheOrNull(Class<?> clazz, String id) {
        return Optional.ofNullable(firstCaches.get(clazz))
                .map(firstCache -> firstCache.get(id))
                .orElse(null);
    }

    public void remove(Class<?> clazz, String instanceId) {
        Optional.ofNullable(firstCaches.get(clazz)).ifPresent(firstCacheMap -> {
            Object firstCache = firstCacheMap.get(instanceId);
            if (firstCache == null) {
                throw new IllegalArgumentException(
                        String.format("Class: %s Id: %s 에 해당하는 일차 캐시가 없습니다.", clazz.getSimpleName(), instanceId));
            }
        });
    }

    private <T> String getInstanceIdAsString(T instance, Field idField) {
        idField.setAccessible(true);

        try {
            return Optional.ofNullable(idField.get(instance)).map(String::valueOf).orElse(null);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
