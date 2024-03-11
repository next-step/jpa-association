package jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import static persistence.sql.constant.SqlConstant.DOT;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class EntityRowMapper<T> implements RowMapper {

    private final Class<T> clazz;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Table table = Table.getInstance(clazz);
            setFieldValues(instance, table.getTableName(), table.getColumns(), resultSet);
            List<Column> eagerRelationColumns = table.getEagerRelationColumns();

            if (eagerRelationColumns.isEmpty()) {
                return instance;
            }
            do {
                setEagerRelationFieldValues(resultSet, eagerRelationColumns, instance);
            } while (resultSet.next());

            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFieldValues(Object instance, String tableName, List<Column> columns, ResultSet resultSet) {
        columns.forEach(column -> column.setFieldValue(instance, ResultSetColumnReader.map(resultSet,
            tableName + DOT.getValue() + column.getColumnName(), column.getType())));
    }

    private void setEagerRelationFieldValues(ResultSet resultSet,
                                        List<Column> eagerRelationColumns,
                                        T instance) {
        eagerRelationColumns.stream()
            .filter(column -> !isEagerRelationEmpty(resultSet, column.getRelationTable()))
            .forEach(column -> {
                Table relationTable = column.getRelationTable();
                Object relatedInstance = relationTable.getClassInstance();
                setFieldValues(relatedInstance, relationTable.getTableName(), relationTable.getColumns(), resultSet);
                column.setFieldValue(instance, relatedInstance);
            });
    }

    private boolean isEagerRelationEmpty(ResultSet resultSet, Table relationTable) {

        String tableName = relationTable.getTableName();
        Column idColumn = relationTable.getIdColumn();

        String key = DOT.concat(tableName, idColumn.getColumnName()).toString();

        Object id = ResultSetColumnReader.map(resultSet, key, idColumn.getType());

        if (Objects.isNull(id)) {
            return true;
        }

        if (id instanceof Number) {
            return ((Number) id).intValue() == 0;
        }

        return false;
    }
}
