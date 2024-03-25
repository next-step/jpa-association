package persistence.sql.common;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jdbc.RowMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DtoMapper<T> implements RowMapper<T> {

    private final Class<T> clazz;

    public DtoMapper(Class<T> clazz) {
        this.clazz = clazz;
    }
    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        T dto;
        try {
            dto = getDto(resultSet);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("DtoMapper가 정상 동작하지 않습니다.", e);
        }
        return dto;
    }

    private T getDto(ResultSet resultSet) throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SQLException {
        T dto;
        dto = clazz.getDeclaredConstructor().newInstance();
        List<Class<? extends Annotation>> invalidAnnotations = List.of(Transient.class, Id.class, OneToMany.class);
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(x -> invalidAnnotations.stream().noneMatch(x::isAnnotationPresent)).collect(Collectors.toList());
        for (Field field : fields) {
            field.setAccessible(true);
            Column annotation = field.getAnnotation(Column.class);
            String columnName = annotation == null || annotation.name().isEmpty()? camelToSnake(field.getName()) : annotation.name();
            Object value = resultSet.getObject(columnName);
            field.set(dto, value);
        }
        return dto;
    }

    private static String camelToSnake(String camelCase) {
        return Pattern.compile("([a-z])([A-Z])")
                .matcher(camelCase)
                .replaceAll(match -> String.format("%s_%s", match.group(1), match.group(2)));
    }
}
