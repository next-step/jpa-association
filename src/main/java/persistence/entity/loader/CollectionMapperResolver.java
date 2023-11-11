package persistence.entity.loader;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.OneToManyField;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public interface CollectionMapperResolver {
    Boolean supports(Field field);

    <T> void map(EntityAttribute entityAttribute, T instance, OneToManyField oneToManyField, ResultSet resultSet);
}
