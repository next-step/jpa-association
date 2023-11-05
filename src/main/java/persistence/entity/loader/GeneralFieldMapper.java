package persistence.entity.loader;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import persistence.entity.attribute.GeneralAttribute;
import persistence.entity.attribute.resolver.GeneralAttributeResolver;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static persistence.entity.attribute.resolver.AttributeResolverHolder.GENERAL_ATTRIBUTE_RESOLVERS;

public class GeneralFieldMapper implements MapperResolver {
    @Override
    public Boolean supports(Field field) {
        return field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class);
    }

    @Override
    public <T> void map(T instance, Field field, ResultSet resultSet) {
        for (GeneralAttributeResolver generalAttributeResolver : GENERAL_ATTRIBUTE_RESOLVERS) {
            if (generalAttributeResolver.supports(field.getType())) {
                field.setAccessible(true);
                GeneralAttribute generalAttribute = generalAttributeResolver.resolve(field);
                try {
                    Object value = resultSet.getObject(generalAttribute.getColumnName());
                    field.set(instance, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
