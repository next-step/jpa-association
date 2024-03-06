package jdbc;

import persistence.sql.meta.AssociationTable;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RowMapperFactory {

    private RowMapperFactory() {
        throw new IllegalArgumentException("RowMapperFactory is a utility class");
    }

    public static <T> RowMapper<T> create(Class<T> clazz) {
        return resultSet -> {
            try {
                T instance = instantiate(clazz);
                Table table = Table.from(clazz);
                setFieldsFromResultSet(instance, table.getColumns(), resultSet);
                if (table.containsAssociation()) {
                    List<AssociationTable> associations = table.getAssociationTables();
                    do {
                        setAssociationFieldsFromResultSet(resultSet, associations, instance);
                    } while (resultSet.next());
                }
                return instance;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of: " + clazz.getName(), e);
            }
        };
    }

    private static <T> T instantiate(Class<T> clazz) throws Exception {
        return clazz.getDeclaredConstructor().newInstance();
    }

    private static void setFieldsFromResultSet(Object instance, List<Column> columns, ResultSet resultSet) throws Exception {
        for (Column column : columns) {
            Field field = column.getField();
            field.setAccessible(true);
            field.set(instance, resultSet.getObject(column.getName()));
        }
    }

    private static <T> void setAssociationFieldsFromResultSet(ResultSet resultSet, List<AssociationTable> associations, T instance) throws Exception {
        for (AssociationTable association : associations) {
            if (association.isEager()) {
                Object associatedInstance = instantiate(association.getClazz());
                setFieldsFromResultSet(associatedInstance, Table.from(association.getClazz()).getColumns(), resultSet);
                addAssociatedInstanceToList(instance, association, associatedInstance);
            }
        }
    }

    private static <T> void addAssociatedInstanceToList(T instance, AssociationTable association, Object associatedInstance) throws IllegalAccessException {
        Field associationField = association.getField();
        associationField.setAccessible(true);
        List<Object> list = (List<Object>) associationField.get(instance);
        if (list == null) {
            list = new ArrayList<>();
            associationField.set(instance, list);
        }
        list.add(associatedInstance);
    }
}
