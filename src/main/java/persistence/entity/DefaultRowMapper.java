package persistence.entity;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jdbc.RowMapper;
import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.OneToManyData;
import persistence.sql.mapping.TableData;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final List<Field> fields;

    public DefaultRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        this.fields = getFields(clazz);
    }

    private List<Field> getFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(OneToMany.class))
                .collect(Collectors.toList());
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        Object entity = createEntity(clazz);
        TableData table = TableData.from(clazz);
        Columns columns = Columns.createColumns(clazz);

        for (Field field : fields) {
            ColumnData columnData = ColumnData.createColumn(table.getName(), field);
            setValue(entity, field, columnData, resultSet);
        }

        if (columns.hasEagerLoad()) {
            mapCollection(resultSet, entity, columns);
        }

        return (T) entity;
    }

    private void mapCollection(ResultSet resultSet, Object entity, Columns columns) throws SQLException {
        for (OneToManyData association : columns.getEagerAssociations()) {
            Field field = association.getField();
            field.setAccessible(true);
            innerSet(entity, field, getChildren(association, resultSet));
        }
    }

    private void setValue(Object entity, Field field, ColumnData columnData, ResultSet resultSet) throws SQLException {
        field.setAccessible(true);
        innerSet(entity, field, resultSet.getObject(columnData.getNameWithTable()));
    }

    private List<Object> getChildren(OneToManyData association, ResultSet resultSet) throws SQLException {
        Class<?> referenceEntityClazz = association.getReferenceEntityClazz();
        TableData table = TableData.from(referenceEntityClazz);

        final List<Object> children = new ArrayList<>();

        do {
            Object childEntity = createEntity(referenceEntityClazz);
            for (Field field : getFields(referenceEntityClazz)) {
                field.setAccessible(true);
                ColumnData column = ColumnData.createColumn(table.getName(), field);
                innerSet(childEntity, field, resultSet.getObject(column.getNameWithTable()));
            }
            children.add(childEntity);
        } while (resultSet.next());
        return children;
    }

    private Object createEntity(Class<?> clazz) throws SQLException {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new SQLException(e);
        }
    }

    private void innerSet(Object entity, Field field, Object value) throws SQLException {
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }
}
