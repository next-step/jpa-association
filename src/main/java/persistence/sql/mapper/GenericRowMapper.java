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
import java.util.function.BiFunction;

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
        Columns columns = new Columns(clazz.getDeclaredFields());

        columns.setGeneralColumn(resultSet, rootEntity, this::setColumnValue);
        JoinTableColumns joinTableColumns = tableColumn.getJoinTableColumns();
        for (JoinTableColumn joinTableColumn : joinTableColumns.getValues()) {
            if(joinTableColumn.getAssociationEntity().isLazy()) {
                continue;
            }
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
            joinTableColumnColumns.setGeneralColumn(resultSet, associatedEntity, this::setColumnValue);
            joinTableColumn.setAssociationColumn(rootEntity, associatedEntity);
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
