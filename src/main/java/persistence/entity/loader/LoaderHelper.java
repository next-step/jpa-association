package persistence.entity.loader;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderHelper {
    private final EntityAttributes entityAttributes;
    private final List<MapperResolver> COLLECTION_MAPPER_RESOLVERS = new ArrayList<>();
    private final List<MapperResolver> MAPPER_RESOLVERS = new ArrayList<>();

    public LoaderHelper(EntityAttributes entityAttributes, CollectionLoader collectionLoader) {
        MAPPER_RESOLVERS.add(new IdFieldMapper());
        MAPPER_RESOLVERS.add(new GeneralFieldMapper());
        COLLECTION_MAPPER_RESOLVERS.add(new EagerLoadingOneToManyFieldMapper(MAPPER_RESOLVERS));
        COLLECTION_MAPPER_RESOLVERS.add(new LazyLoadingOneToManyFieldMapper(entityAttributes, collectionLoader));

        this.entityAttributes = entityAttributes;
    }

    public <T> T mapResultSetToEntity(Class<T> clazz, ResultSet resultSet) {
        try {
            T instance = instantiateClass(clazz);

            if (!resultSet.next()) {
                return null;
            }

            mapAttributes(clazz, resultSet, instance);

            do {
                mapCollectionAttributes(clazz, resultSet, instance);
            } while (resultSet.next());

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> mapResultSetToList(Class<T> clazz, ResultSet resultSet) {
        Map<String, T> loadedEntities = new HashMap<>();

        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(clazz);
        try {
            if (!resultSet.next()) {
                return null;
            }

            while (resultSet.next()) {
                String id = String.valueOf(resultSet.getObject(entityAttribute.getIdAttribute().getColumnName()));
                T instance = loadedEntities.get(id);

                if (instance == null) {
                    instance = instantiateClass(clazz);
                    mapAttributes(clazz, resultSet, instance);
                }

                mapCollectionAttributes(clazz, resultSet, instance);
                loadedEntities.put(id, instance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>(loadedEntities.values());
    }

    private <T> void mapAttributes(Class<?> clazz, ResultSet resultSet, T instance) {
        for (Field field : clazz.getDeclaredFields()) {
            mapAttribute(resultSet, instance, field);
        }
    }

    private <T> void mapAttribute(ResultSet resultSet, T instance, Field field) {
        for (MapperResolver mapperResolver : MAPPER_RESOLVERS) {
            if (mapperResolver.supports(field)) {
                mapperResolver.map(instance, field, resultSet);
            }
        }
    }

    private <T> void mapCollectionAttributes(Class<T> clazz, ResultSet resultSet, T instance) {
        for (Field field : clazz.getDeclaredFields()) {
            mapCollectionAttribute(resultSet, instance, field);
        }
    }

    private <T> void mapCollectionAttribute(ResultSet resultSet, T instance, Field field) {
        for (MapperResolver mapperResolver : COLLECTION_MAPPER_RESOLVERS) {
            if (mapperResolver.supports(field)) {
                mapperResolver.map(instance, field, resultSet);
            }
        }
    }

    private <T> T instantiateClass(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("[%s] 클래스 초기화 실패", clazz.getSimpleName()), e);
        }
    }
}
