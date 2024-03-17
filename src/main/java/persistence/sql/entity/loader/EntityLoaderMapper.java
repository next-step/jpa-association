package persistence.sql.entity.loader;

import jakarta.persistence.JoinColumn;
import persistence.sql.dml.exception.FieldSetValueException;
import persistence.sql.dml.exception.InstanceException;
import persistence.sql.dml.exception.InvalidFieldValueException;
import persistence.sql.dml.exception.NotFoundFieldException;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class EntityLoaderMapper {

    private EntityLoaderMapper() {}

    private static class EntityLoaderMapperSingleton {
        private static final EntityLoaderMapper ENTITY_LOADER_MAPPER = new EntityLoaderMapper();
    }

    public static EntityLoaderMapper getInstance() {
        return EntityLoaderMapperSingleton.ENTITY_LOADER_MAPPER;
    }

    public <T> T mapper(Class<T> clazz, ResultSet resultSet) {
        EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        T instance = createInstance(clazz);

        Spliterator<DomainType> spliterator = entityMappingTable.getDomainTypes().spliterator();
        StreamSupport.stream(spliterator, false)
                .forEach(domainType -> {
                    Field field = getField(clazz, domainType.getName());
                    setField(instance, field, getValue(resultSet, domainType.getColumnName()));
                });

        return instance;
    }

    public <T> T eagerMapper(Class<T> clazz, ResultSet resultSet) {
        EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        T instance = createInstance(clazz);

        Spliterator<DomainType> spliterator = entityMappingTable.getDomainTypes().spliterator();
        StreamSupport.stream(spliterator, false)
                .forEach(domainType -> {
                    Field field = getField(clazz, domainType.getName());

                    if(field.isAnnotationPresent(JoinColumn.class)) {
                        List<Object> result = subObjectMapping(resultSet, domainType);

                        setField(instance, field, result);
                        return;
                    }

                    setField(instance, field, getValue(resultSet, domainType.getAlias(entityMappingTable.getTableName().getAlias())));
                });

        return instance;
    }

    private List<Object> subObjectMapping(ResultSet resultSet, DomainType domainType) {
        List<Object> result = new ArrayList<>();

        Class<?> subClass = (Class<?>) ((ParameterizedType) domainType.getField().getGenericType()).getActualTypeArguments()[0];
        EntityMappingTable subEntity = EntityMappingTable.from(subClass);

        try {
            do {
                Object subInstance = createInstance(subClass);

                Spliterator<DomainType> subSpliterator = subEntity.getDomainTypes().spliterator();
                StreamSupport.stream(subSpliterator, false)
                        .forEach(subDomainType -> {
                            Field subField = getField(subClass, subDomainType.getName());
                            setField(subInstance,
                                    subField,
                                    getValue(resultSet, subEntity.getTableName().getName() + "." + subDomainType.getColumnName()));
                        });
                result.add(subInstance);
            } while(resultSet.next());
        } catch (SQLException e) {
            throw new InstanceException();
        }
        return result;
    }

    private <T> T createInstance(final Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new InstanceException();
        }
    }

    private Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (Exception e) {
            throw new NotFoundFieldException();
        }
    }

    private Object getValue(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getObject(columnName);
        } catch (Exception e) {
            throw new InvalidFieldValueException();
        }
    }

    private void setField(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            throw new FieldSetValueException();
        }
    }
}
