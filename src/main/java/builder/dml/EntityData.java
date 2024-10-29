package builder.dml;

import jakarta.persistence.*;
import util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityData {

    private final static String PK_NOT_EXIST_MESSAGE = "PK 컬럼을 찾을 수 없습니다.";
    private final static String NOT_EXIST_ENTITY_ANNOTATION = "@Entity 어노테이션이 존재하지 않습니다.";
    private final static String GET_FIELD_VALUE_ERROR_MESSAGE = "필드 값을 가져오는 중 에러가 발생했습니다.";
    private final static String COMMA = ", ";
    private final static String EQUALS = "=";

    private final String tableName;
    private List<DMLColumnData> columns;
    private final String pkName;
    private final Object id;
    private final Class<?> clazz;
    private Object entityInstance;

    private EntityData(Object entityInstance) {
        this.clazz = entityInstance.getClass();
        confirmEntityAnnotation(this.clazz);
        this.tableName = getTableName(this.clazz);
        this.columns = getInstanceColumnData(entityInstance);
        this.id = getPkValue();
        this.pkName = getPkName();
        this.entityInstance = deepCopy(entityInstance);
    }

    private <T> EntityData(Class<T> clazz, Object id) {
        confirmEntityAnnotation(clazz);
        this.clazz = clazz;
        this.tableName = getTableName(clazz);
        this.columns = getEntityColumnData(clazz);
        this.id = id;
        this.pkName = getPkName();
    }

    public static EntityData createEntityData(Object entityInstance) {
        return new EntityData(entityInstance);
    }

    public static <T> EntityData createEntityData(Class<T> clazz, Object id) {
        return new EntityData(clazz, id);
    }

    public String getTableName() {
        return tableName;
    }

    public Object getId() {
        return id;
    }

    public String getPkNm() {
        return this.pkName;
    }

    public List<DMLColumnData> getColumns() {
        return columns;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getEntityInstance() {
        return entityInstance;
    }

    public String wrapString() {
        return (this.id instanceof String) ? StringUtil.wrapSingleQuote(this.id) : String.valueOf(this.id);
    }

    // 테이블 열 정의 생성
    public String getColumnDefinitions() {
        return this.columns.stream()
                .filter(column -> !column.isPrimaryKey())
                .map(column -> column.getColumnName() + EQUALS + column.getColumnValueByType())
                .collect(Collectors.joining(COMMA));
    }

    // 테이블 컬럼명 생성
    public String getColumnNames() {
        return this.columns.stream()
                .map(DMLColumnData::getColumnName)
                .collect(Collectors.joining(COMMA));
    }

    //테이블 컬럼 Value 값들 생성
    public String getColumnValues() {
        return this.columns.stream()
                .map(dmlColumnData -> {
                    Object value = dmlColumnData.getColumnValue();
                    if (dmlColumnData.getColumnType() == String.class) { //데이터 타입이 String 이면 작은 따옴표로 묶어준다.
                        return StringUtil.wrapSingleQuote(value);
                    }
                    return String.valueOf(value);
                })
                .collect(Collectors.joining(COMMA));
    }

    //PkName를 가져온다.
    public String getPkName() {
        return this.columns.stream()
                .filter(DMLColumnData::isPrimaryKey)
                .map(DMLColumnData::getColumnName)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(PK_NOT_EXIST_MESSAGE));
    }

    public EntityData changeColumns(List<DMLColumnData> columns) {
        this.columns = columns;
        return this;
    }

    public List<DMLColumnData> getDifferentColumns(EntityData snapShotBuilderData) {
        Map<String, DMLColumnData> snapShotColumnMap = convertDMLColumnDataMap(snapShotBuilderData);

        return this.columns.stream()
                .filter(entityColumn -> {
                    DMLColumnData persistenceColumn = snapShotColumnMap.get(entityColumn.getColumnName());
                    return !entityColumn.getColumnValue().equals(persistenceColumn.getColumnValue());
                })
                .toList();
    }

    private Map<String, DMLColumnData> convertDMLColumnDataMap(EntityData EntityData) {
        return EntityData.getColumns().stream()
                .collect(Collectors.toMap(DMLColumnData::getColumnName, Function.identity()));
    }

    private List<DMLColumnData> getEntityColumnData(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        List<DMLColumnData> DMLColumnDataList = new ArrayList<>();
        for (Field field : fields) {
            getEntityPrimaryKey(DMLColumnDataList, field);
            createDMLEntityColumnData(DMLColumnDataList, field);
        }
        return DMLColumnDataList;
    }

    private <T> List<DMLColumnData> getInstanceColumnData(T entityInstance) {
        Field[] fields = this.clazz.getDeclaredFields();
        List<DMLColumnData> DMLColumnDataList = new ArrayList<>();
        for (Field field : fields) {
            getInstancePrimaryKey(DMLColumnDataList, field, entityInstance);
            createDMLInstanceColumnData(DMLColumnDataList, field, entityInstance);
        }
        return DMLColumnDataList;
    }

    private void getEntityPrimaryKey(List<DMLColumnData> DMLColumnDataList, Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            DMLColumnDataList.add(DMLColumnData.creatInstancePkColumn(field.getName(), field.getType()));
        }
    }

    private <T> void getInstancePrimaryKey(List<DMLColumnData> DMLColumnDataList, Field field, T entityInstance) {
        try {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                DMLColumnDataList.add(DMLColumnData.creatEntityPkColumn(field.getName(), field.getType(), field.get(entityInstance)));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
        }
    }

    private void createDMLEntityColumnData(List<DMLColumnData> DMLColumnDataList, Field field) {
        if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Id.class))
            return; // @Transient인 경우 검증하지 않음

        String columnName = field.getName();

        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }

        DMLColumnDataList.add(DMLColumnData.createEntityColumn(columnName));
    }

    private <T> void createDMLInstanceColumnData(List<DMLColumnData> DMLColumnDataList, Field field, T entityInstance) {
        if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Id.class))
            return; // @Transient인 경우 검증하지 않음

        String columnName = field.getName();

        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }

        field.setAccessible(true);

        try {
            DMLColumnDataList.add(DMLColumnData.creatInstanceColumn(columnName, field.getType(), field.get(entityInstance)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
        }
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

    private Object getPkValue() {
        return this.columns.stream()
                .filter(DMLColumnData::isPrimaryKey)
                .findFirst()
                .map(DMLColumnData::getColumnValue)
                .orElseThrow(() -> new IllegalArgumentException(PK_NOT_EXIST_MESSAGE));
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

}
