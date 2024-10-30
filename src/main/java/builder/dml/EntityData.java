package builder.dml;

import jakarta.persistence.*;
import util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityData {

    private final static String NOT_EXIST_ENTITY_ANNOTATION = "@Entity 어노테이션이 존재하지 않습니다.";

    private final Class<?> clazz;
    private final String tableName;
    private final String alias;
    private final String pkName;
    private Object id;
    private final EntityColumn entityColumn;
    private JoinStatus joinStatus;
    private Object entityInstance;

    // Constructor
    private EntityData(Object entityInstance) {
        this.clazz = entityInstance.getClass();
        confirmEntityAnnotation(this.clazz);
        this.joinStatus = JoinStatus.FALSE;
        this.tableName = getTableName(this.clazz);
        this.alias = getAlias();
        this.entityColumn = new EntityColumn(entityInstance, this.clazz);
        this.id = this.entityColumn.getPkValue();
        this.pkName = this.entityColumn.getPkName();
        this.entityInstance = deepCopy(entityInstance);
    }

    private <T> EntityData(Class<T> clazz, Object id) {
        confirmEntityAnnotation(clazz);
        this.joinStatus = JoinStatus.FALSE;
        this.clazz = clazz;
        this.tableName = getTableName(clazz);
        this.alias = getAlias();
        this.entityColumn = new EntityColumn(clazz);
        this.id = id;
        this.pkName = this.entityColumn.getPkName();
    }

    private <T> EntityData(Class<T> clazz) {
        confirmEntityAnnotation(clazz);
        this.joinStatus = JoinStatus.FALSE;
        this.clazz = clazz;
        this.tableName = getTableName(clazz);
        this.alias = getAlias();
        this.entityColumn = new EntityColumn(clazz);
        this.pkName = this.entityColumn.getPkName();
    }

    // Static Factory Methods
    public static EntityData createEntityData(Object entityInstance) {
        return new EntityData(entityInstance);
    }

    public static <T> EntityData createEntityData(Class<T> clazz, Object id) {
        return new EntityData(clazz, id);
    }

    public static <T> EntityData createEntityData(Class<T> clazz) {
        return new EntityData(clazz);
    }

    public String getTableName() {
        if (this.joinStatus.isTrue()) {
            return this.tableName + " " + this.alias;
        }
        return this.tableName;
    }

    public Object getId() {
        return id;
    }

    public String getPkNm() {
        if (this.joinStatus.isTrue()) {
            return this.alias + "." + this.pkName;
        }
        return this.pkName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getEntityInstance() {
        return entityInstance;
    }

    public EntityColumn getEntityColumn() {
        return entityColumn;
    }

    public boolean checkJoin() {
        return this.joinStatus.isTrue();
    }

    public String wrapString() {
        return (this.id instanceof String) ? StringUtil.wrapSingleQuote(this.id) : String.valueOf(this.id);
    }

    public EntityData changeColumns(List<DMLColumnData> columns) {
        this.entityColumn.changeColumns(columns);
        return this;
    }

    public String getColumnNames() {
        return this.entityColumn.getColumnNames();
    }

    public String getColumnValues() {
        return this.entityColumn.getColumnValues();
    }

    public String getColumnDefinitions() {
        return this.entityColumn.getColumnDefinitions();
    }

    public List<DMLColumnData> getDifferentColumns(EntityData snapshotEntityData) {
        return this.entityColumn.getDifferentColumns(snapshotEntityData);
    }

    public Map<String, DMLColumnData> convertDMLColumnDataMap() {
        return this.entityColumn.getColumns().stream()
                .collect(Collectors.toMap(DMLColumnData::getColumnName, Function.identity()));
    }

    private void confirmEntityAnnotation(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_EXIST_ENTITY_ANNOTATION);
        }
    }

    private String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return table.name();
        }
        return entityClass.getSimpleName();
    }

    private Object deepCopy(Object original) {
        if (original == null) return null;

        try {
            Class<?> clazz = original.getClass();
            Object copy = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Object value = field.get(original);
                field.set(copy, value);
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

    private String getAlias() {
        String alias = this.tableName.substring(0, 1).toLowerCase();

//        int suffix = 2;
//        while (SqlKeyword.isKeyword(alias) || this.otherAlias.contains(alias)) {
//            alias = this.tableName.substring(0, suffix);
//            suffix++;
//        }
//
//        this.otherAlias.add(alias);
        return alias;
    }

    private void joinStatusTrue() {
        this.joinStatus = JoinStatus.TRUE;
    }
}