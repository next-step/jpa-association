package persistence.entity.loader;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;

import static persistence.entity.loader.MapperResolverHolder.COLLECTION_MAPPER_RESOLVERS;
import static persistence.entity.loader.MapperResolverHolder.MAPPER_RESOLVERS;

public class SimpleEntityLoaderImpl implements EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final EntityAttributes entityAttributes;

    public SimpleEntityLoaderImpl(JdbcTemplate jdbcTemplate,
                                  EntityAttributes entityAttributes) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityAttributes = entityAttributes;
    }

    @Override
    public <T> T load(Class<T> clazz, String id) {
        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(clazz);
        IdAttribute idAttribute = entityAttribute.getIdAttribute();

        String sql = SelectQueryBuilder.of(entityAttribute)
                .where(entityAttribute.getTableName(), idAttribute.getColumnName(), id)
                .prepareStatement();

        return jdbcTemplate.queryForObject(sql,
                rs -> mapResultSetToEntity(clazz, rs));
    }

    private <T> T mapResultSetToEntity(Class<T> clazz, ResultSet resultSet) {
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

    private <T> void mapAttributes(Class<T> clazz, ResultSet resultSet, T instance) {
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
