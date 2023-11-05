package persistence.entity.loader;

import jakarta.persistence.Id;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.resolver.IdAttributeResolver;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static persistence.entity.attribute.resolver.AttributeResolverHolder.ID_ATTRIBUTE_RESOLVERS;

public class IdFieldMapper implements MapperResolver {

    @Override
    public Boolean supports(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    @Override
    public <T> void map(T instance, Field field, ResultSet resultSet) {
        for (IdAttributeResolver idAttributeResolver : ID_ATTRIBUTE_RESOLVERS) {
            if (idAttributeResolver.supports(field.getType())) {
                field.setAccessible(true);
                IdAttribute idAttribute = idAttributeResolver.resolve(field);
                try {
                    Object value = resultSet.getObject(idAttribute.getColumnName());
                    field.set(instance, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
