package persistence.sql.mapper;

import jdbc.RowMapper;
import persistence.exception.CanNotFindDeclaredConstructorException;
import persistence.exception.CanNotGetObjectException;
import persistence.sql.column.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GenericRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;

    public GenericRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        T rootEntity = createInstance(clazz);

        TableColumn tableColumn = new TableColumn(clazz);
        setIdColumn(resultSet, rootEntity, new IdColumn(clazz.getDeclaredFields()));
        setGeneralColumn(resultSet, rootEntity, new Columns(clazz.getDeclaredFields()));
        for (JoinTableColumn joinTableColumn : tableColumn.getJoinTableColumn()) {
            setAssociatedEntity(resultSet, joinTableColumn, rootEntity);
        }
        return rootEntity;
    }

    private void setAssociatedEntity(ResultSet resultSet, JoinTableColumn joinTableColumn, T rootEntity) throws SQLException {
        do {
            Columns joinTableColumnColumns = joinTableColumn.getColumns();
            IdColumn joinTableColumnIdColumn = joinTableColumn.getIdColumn();

            T associatedEntity = createInstance((Class<T>) joinTableColumn.getClazz());

            setIdColumn(resultSet, associatedEntity, joinTableColumnIdColumn);
            setGeneralColumn(resultSet, associatedEntity, joinTableColumnColumns);
            setAssociationColumn(joinTableColumn, rootEntity, associatedEntity);
        } while (resultSet.next());
    }

    private T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new CanNotFindDeclaredConstructorException("[ERROR] 생성자를 찾을 수 없습니다.", e);
        }
    }

    private void setIdColumn(ResultSet resultSet, T instance, IdColumn idColumn) {
        setColumnValue(resultSet, instance, idColumn);
    }

    private void setGeneralColumn(ResultSet resultSet, T instance, Columns columns) {
        columns.getValues().stream()
                .filter(column -> !column.isAssociationEntity())
                .forEach(column -> setColumnValue(resultSet, instance, column));
    }

    private void setAssociationColumn(JoinTableColumn joinTableColumn, T rootEntity, T associatedEntity) {
        String joinFieldName = joinTableColumn.getAssociationEntity().getJoinFieldName();
        Field associationField = getDeclaredField(clazz, joinFieldName);
        associationField.setAccessible(true);
        Collection<T> associationCollection = getAssociationCollection(associationField, rootEntity);
        associationCollection.add(associatedEntity);
    }

    private Collection<T> getAssociationCollection(Field associationField, T rootEntity) {
        try {
            Collection<T> associationCollection = (Collection<T>) associationField.get(rootEntity);
            if (associationCollection == null) {
                associationCollection = new ArrayList<>();
                associationField.set(rootEntity, associationCollection);
            }
            return associationCollection;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getDeclaredField(Class<T> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private void setColumnValue(ResultSet resultSet, T instance, Column column) {
        String columnName = column.getName();
        TableColumn tableColumn = new TableColumn(instance.getClass());
        Field field = column.getField();
        field.setAccessible(true);
        try {
            field.set(instance, resultSet.getObject(tableColumn.getName() + "." + columnName));
        } catch (IllegalAccessException | SQLException e) {
            throw new CanNotGetObjectException("[ERROR] field의 값을 불러오는데 실패했습니다. object: " + columnName, e);
        }
    }
}
