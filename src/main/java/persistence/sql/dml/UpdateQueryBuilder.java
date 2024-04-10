package persistence.sql.dml;

import pojo.EntityColumn;
import pojo.EntityMetaData;
import pojo.FieldInfos;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static constants.CommonConstants.AND;
import static constants.CommonConstants.COMMA;
import static constants.CommonConstants.EQUAL;
import static utils.StringUtils.joinNameAndValue;

public class UpdateQueryBuilder {

    private static final String INSERT_DATA_QUERY = "INSERT INTO %s (%s) VALUES (%s);";
    private static final String UPDATE_DATA_QUERY = "UPDATE %s SET %s WHERE %s;";

    private final EntityMetaData entityMetaData;

    public UpdateQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public String insertQuery(Object entity) {
        return String.format(INSERT_DATA_QUERY, entityMetaData.getEntityName(), columnsClause(entity), valuesClause(entity));
    }

    public String updateQuery(Object entity) {
        return String.format(UPDATE_DATA_QUERY, entityMetaData.getEntityName(), setClause(entity), whereClause(entity));
    }

    private String columnsClause(Object entity) {
        return new FieldInfos(entity.getClass().getDeclaredFields()).getIdAndColumnFields().stream()
                .map(field -> new EntityColumn(field, entity))
                .map(fieldInfo -> fieldInfo.getFieldName().getName())
                .reduce((o1, o2) -> String.join(COMMA, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));
    }

    private String valuesClause(Object entity) {
        return new FieldInfos(entity.getClass().getDeclaredFields()).getIdAndColumnFields().stream()
                .map(field -> new EntityColumn(field, entity))
                .map(fieldInfo -> fieldInfo.getFieldValue().getValue())
                .reduce((o1, o2) -> String.join(COMMA, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));
    }

    private String setClause(Object entity) {
        List<Field> columnFields = new FieldInfos(entity.getClass().getDeclaredFields()).getColumnFields();
        return fieldNameAndValueClause(entity, columnFields, COMMA);
    }

    private String whereClause(Object entity) {
        Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
        return fieldNameAndValueClause(entity, List.of(field), AND);
    }

    private String fieldNameAndValueClause(Object entity, List<Field> fields, String delimiter) {
        return fields.stream()
                .map(field -> new EntityColumn(field, entity))
                .filter(fieldInfo -> Objects.nonNull(fieldInfo.getFieldName()) && Objects.nonNull(fieldInfo.getFieldValue()))
                .map(fieldInfo ->
                        joinNameAndValue(EQUAL, fieldInfo.getFieldName().getName(), String.valueOf(fieldInfo.getFieldValue().getValue())))
                .reduce((o1, o2) -> String.join(delimiter, o1, o2))
                .orElseThrow(() -> new IllegalStateException("update 데이터가 없습니다."));
    }
}
