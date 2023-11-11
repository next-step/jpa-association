package persistence.entity.loader;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.OneToManyField;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class EagerLoadingOneToManyFieldMapper implements CollectionMapperResolver {
    private final LoaderMapper loaderMapper;

    public EagerLoadingOneToManyFieldMapper(LoaderMapper loaderMapper) {
        this.loaderMapper = loaderMapper;
    }

    @Override
    public Boolean supports(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).fetch() == FetchType.EAGER;
        }
        return false;
    }

    @Override
    public void map(EntityAttribute entityAttribute, Object instance, OneToManyField oneToManyField, ResultSet resultSet) {
        Field field = oneToManyField.getField();

        try {
            field.setAccessible(true);

            Collection<Object> collection = getOrCreateCollection(instance, field);
            Object oneToManyFieldInstance = mapResultSetToOneToManyAnnotatedField(resultSet, oneToManyField.getEntityAttribute());
            collection.add(oneToManyFieldInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<Object> getOrCreateCollection(Object entityInstance, Field field) throws IllegalAccessException {
        Collection<Object> collection = (Collection<Object>) field.get(entityInstance);
        if (collection == null) {
            collection = new ArrayList<>();
            field.set(entityInstance, collection);
        }
        return collection;
    }

    private Object mapResultSetToOneToManyAnnotatedField(ResultSet resultSet, EntityAttribute oneToManyFieldEntityAttribute) {
        try {
            Object oneToManyFieldInstance = oneToManyFieldEntityAttribute.getClazz().getConstructor().newInstance();

            loaderMapper.mapResultSetToAttributes(oneToManyFieldEntityAttribute, resultSet, oneToManyFieldInstance);

            return oneToManyFieldInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
