package persistence.sql.dml.insert;

import persistence.entity.EntityUtils;
import persistence.sql.NameUtils;

import java.lang.reflect.Field;

public class InsertQueryBuilder {
    private InsertQueryBuilder() {
    }

    public static String generateQuery(Object entity) {
        String tableName = NameUtils.getTableName(entity.getClass());
        String columnClause = columnClause(entity.getClass());
        String valueClause = valueClause(entity);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("insert into ")
                .append(tableName)
                .append(columnClause)
                .append(" values ")
                .append(valueClause)
                .append(";");
        return stringBuilder.toString();
    }

    private static String columnClause(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder(" (");

        Field[] managedFields = EntityUtils.getManagedFieldsExceptId(clazz);
        for (Field field : managedFields) {
            stringBuilder
                    .append(NameUtils.getColumnName(field))
                    .append(", ");
        }

        stringBuilder.setLength(stringBuilder.length() - 2);
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    private static String valueClause(Object entity) {
        StringBuilder stringBuilder = new StringBuilder("(");

        Class<?> clazz = entity.getClass();
        Field[] fields = EntityUtils.getManagedFieldsExceptId(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = EntityUtils.getFieldValue(field, entity);
            if (fieldValue == null) {
                stringBuilder.append("null, ");
                continue;
            }
            stringBuilder
                    .append("'")
                    .append(fieldValue)
                    .append("', ");
        }

        stringBuilder.setLength(stringBuilder.length() - 2);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
