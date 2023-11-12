package jdbc;

import persistence.sql.dml.builder.SelectQueryBuilder;
import persistence.sql.meta.ColumnMeta;
import persistence.sql.meta.EntityMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class JoinEntityRowMapper<T> implements RowMapper<T> {

    private final Class clazz;
    private final int startRowIndex;
    private final int endRowIndex;

    private JoinEntityRowMapper(Class<T> clazz, int startColumnIndex, int endColumnIndex) {
        this.clazz = clazz;
        this.startRowIndex = startColumnIndex;
        this.endRowIndex = endColumnIndex;
    }

    public static <T> JoinEntityRowMapper<T> of(EntityMeta entityMeta, String selectQuery) {
        List<String> selectColumns = SelectQueryBuilder.extractSelectColumns(selectQuery);
        int startColumnIndex = extractStartColumnIndex(entityMeta, selectColumns);
        int endColumnIndex = extractEndColumnIndex(entityMeta, selectColumns);
        return new JoinEntityRowMapper(entityMeta.getInnerClass(), startColumnIndex, endColumnIndex);
    }

    private static int extractStartColumnIndex(EntityMeta entityMeta, List<String> selectColumns) {
        return IntStream.range(0, selectColumns.size())
                .filter(i -> selectColumns.get(i).startsWith(entityMeta.getTableName()))
                .findFirst()
                .orElse(selectColumns.size());
    }

    private static int extractEndColumnIndex(EntityMeta entityMeta, List<String> selectColumns) {
        return IntStream.range(0, selectColumns.size())
                .filter(i -> selectColumns.get(i).startsWith(entityMeta.getTableName()))
                .reduce((first, second) -> second)
                .orElse(-1);
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            Map<String, Object> resultValueMap = buildResultValueMap(resultSet);
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T entityInstance = constructor.newInstance();
            Arrays.stream(clazz.getDeclaredFields())
                    .forEach(field -> setFieldValue(entityInstance, resultValueMap, field));
            return entityInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> buildResultValueMap(ResultSet resultSet) throws SQLException {
        Map<String, Object> resultValueMap = new HashMap<>();
        for (int i = startRowIndex; i <= endRowIndex; i++) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            String columnName = metaData.getColumnName(i);
            resultValueMap.put(columnName.toLowerCase(), resultSet.getObject(i));
        }
        return resultValueMap;
    }

    private void setFieldValue(T entityInstance, Map<String, Object> resultValueMap, Field field) {
        ColumnMeta columnMeta = ColumnMeta.of(field);
        if (columnMeta.isTransient()) {
            return;
        }
        field.setAccessible(true);
        try {
            Object fieldValue = resultValueMap.get(columnMeta.getColumnName());
            field.set(entityInstance, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
