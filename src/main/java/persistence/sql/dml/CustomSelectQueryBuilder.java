package persistence.sql.dml;

import pojo.EntityJoinMetaData;
import pojo.EntityMetaData;
import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import static constants.CommonConstants.COMMA;

public class CustomSelectQueryBuilder {

    private static final String FIND_BY_ID_JOIN_QUERY = "SELECT %s FROM %s LEFT JOIN %s ON %s = %s WHERE %s = %s;";

    private final EntityJoinMetaData entityJoinMetaData; //Order
    private final EntityMetaData owner; //OrderItem

    public CustomSelectQueryBuilder(EntityJoinMetaData entityJoinMetaData) {
        this.entityJoinMetaData = entityJoinMetaData;
        this.owner = entityJoinMetaData.getOwner();
    }

    public String findByIdJoinQuery1(Object entity, Class<?> clazz, Object condition) {
        Field field = new FieldInfos(clazz.getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);

        return String.format(FIND_BY_ID_JOIN_QUERY, getSelectData(),
                entityJoinMetaData.getEntityName(), owner.getEntityName(),
                entityJoinMetaData.getEntityName() + "." + idField.getFieldNameData(),
                owner.getEntityName() + "." + entityJoinMetaData.joinColumnName(),
                entityJoinMetaData.getEntityName() + "." + idField.getFieldNameData(), condition);
    }

    /**
     * 아래 방법을 고민 중이라서 한번 피드백 부탁드립니다.
     */
    public String findByIdJoinQuery2(Object entity, Class<?> clazz, Object condition) {
        Field field = new FieldInfos(clazz.getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);

        Field joinColumnField = new FieldInfos(clazz.getDeclaredFields()).getJoinColumnField();
        Class<?> subClass = (Class<?>) ((ParameterizedType) joinColumnField.getGenericType()).getActualTypeArguments()[0];
        EntityMetaData owner = new EntityMetaData(subClass, null);

        return String.format(FIND_BY_ID_JOIN_QUERY, getSelectData(),
                entityJoinMetaData.getEntityName(), owner.getEntityName(),
                entityJoinMetaData.getEntityName() + "." + idField.getFieldNameData(),
                owner.getEntityName() + "." + entityJoinMetaData.joinColumnName(),
                entityJoinMetaData.getEntityName() + "." + idField.getFieldNameData(), condition);
    }

    private String getSelectData() {
        String entityData = entityJoinMetaData.getEntityColumns()
                .stream()
                .map(entityColumn -> entityColumn.getFieldName().getName())
                .map(name -> entityJoinMetaData.getEntityName() + "." + name)
                .reduce((o1, o2) -> String.join(COMMA, o1, o2))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));

        String ownerEntityData = owner.getEntityColumns()
                .stream()
                .map(entityColumn -> entityColumn.getFieldName().getName())
                .map(name -> owner.getEntityName() + "." + name)
                .reduce((o1, o2) -> String.join(COMMA, o1, o2))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));

        return entityData + COMMA + ownerEntityData;
    }

}
