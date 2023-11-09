package persistence.entity.loader;

import persistence.entity.attribute.OneToManyField;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public interface CollectionMapperResolver {
    Boolean supports(Field field);

    <T> void map(T instance, OneToManyField oneToManyField, ResultSet resultSet);
}
