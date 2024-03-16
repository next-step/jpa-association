package database;

import jdbc.RowMapper;
import persistence.entity.common.EntityBinder;
import persistence.entity.common.EntityMetaCache;
import persistence.sql.model.BaseColumn;
import persistence.sql.model.JoinColumn;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class EntityRowMapper<T> implements RowMapper<T> {

    private final Class<T> clazz;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        Table table = entityMetaCache.getTable(clazz);

        T instance = createInstance(clazz);
        EntityBinder entityBinder = new EntityBinder(instance);

        bindResultSet(resultSet, table, entityBinder);
        bindJoinEntities(resultSet, table, entityBinder);

        return instance;
    }

    private T createInstance(Class<T> clazz) {
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void bindResultSet(ResultSet resultSet, Table table, EntityBinder entityBinder) {
        PKColumn pkColumn = table.getPKColumn();
        bindColumn(resultSet, pkColumn, entityBinder);

        table.getColumns()
                .stream()
                .forEach(column -> bindColumn(resultSet, column, entityBinder));
    }

    private void bindColumn(ResultSet resultSet, BaseColumn column, EntityBinder entityBinder) {
        try {
            String columnName = column.getName();
            Object columnValue = resultSet.getObject(columnName);
            entityBinder.bindValue(column, columnValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void bindJoinEntities(ResultSet resultSet, Table table, EntityBinder entityBinder) {
        Map<JoinColumn, Collection<Object>> joinEntityLists = new HashMap<>();

        List<JoinColumn> joinColumns = table.getJoinColumns();
        List<JoinColumn> eagerJoinColumns = joinColumns.stream()
                .filter(joinColumn -> !joinColumn.isLazy())
                .collect(Collectors.toUnmodifiableList());

        eagerJoinColumns.forEach(joinColumn -> joinEntityLists.put(joinColumn, new ArrayList<>()));

        do {
            eagerJoinColumns.forEach(joinColumn -> {
                Collection<Object> joinEntityList = joinEntityLists.get(joinColumn);
                Object joinInstance = getJoinInstance(resultSet, joinColumn);
                joinEntityList.add(joinInstance);
            });
        } while (hasNext(resultSet));

        joinColumns.forEach(joinColumn -> {
            Collection<Object> joinEntityList = joinEntityLists.get(joinColumn);
            entityBinder.bindValue(joinColumn, joinEntityList);
        });
    }

    private boolean hasNext(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getJoinInstance(ResultSet resultSet, JoinColumn joinColumn) {
        Table joinTable = joinColumn.getTable();
        Class<?> clazz = joinTable.getEntity();

        Object joinInstance = createJoinInstance(clazz);
        EntityBinder joinEntityBinder = new EntityBinder(joinInstance);
        bindResultSet(resultSet, joinTable, joinEntityBinder);

        return joinInstance;
    }

    private Object createJoinInstance(Class<?> clazz) {
        try {
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
