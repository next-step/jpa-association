package builder.dml;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityColumn {

    private final static String PK_NOT_EXIST_MESSAGE = "PK 컬럼을 찾을 수 없습니다.";
    private static final String GET_FIELD_VALUE_ERROR_MESSAGE = "필드 값을 가져오는 중 에러가 발생했습니다.";
    private final static String COMMA = ", ";
    private final static String EQUALS = "=";

    private final Class<?> clazz;
    private List<DMLColumnData> columns;

    public EntityColumn(Object entityInstance, Class<?> clazz) {
        this.clazz = clazz;
        this.columns = getInstanceColumnData(entityInstance);
    }

    public EntityColumn(Class<?> clazz) {
        this.clazz = clazz;
        this.columns = getEntityColumnData(clazz);
    }

    public List<DMLColumnData> getColumns() {
        return columns;
    }

    public String getColumnDefinitions() {
        return this.columns.stream()
                .filter(column -> !column.isPrimaryKey())
                .map(column -> column.getColumnName() + EQUALS + column.getColumnValueByType())
                .collect(Collectors.joining(COMMA));
    }

    public String getColumnValues() {
        return this.columns.stream()
                .map(dmlColumnData -> {
                    Object value = dmlColumnData.getColumnValue();
                    if (dmlColumnData.getColumnType() == String.class) { // 데이터 타입이 String이면 작은 따옴표로 묶어준다.
                        return StringUtil.wrapSingleQuote(value);
                    }
                    return String.valueOf(value);
                })
                .collect(Collectors.joining(COMMA));
    }

    public String getPkName() {
        return this.columns.stream()
                .filter(DMLColumnData::isPrimaryKey)
                .map(DMLColumnData::getColumnName)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(PK_NOT_EXIST_MESSAGE));
    }

    public Object getPkValue() {
        return this.columns.stream()
                .filter(DMLColumnData::isPrimaryKey)
                .findFirst()
                .map(DMLColumnData::getColumnValue)
                .orElseThrow(() -> new IllegalArgumentException(PK_NOT_EXIST_MESSAGE));
    }

    public List<DMLColumnData> getDifferentColumns(EntityData snapShotBuilderData) {
        Map<String, DMLColumnData> snapShotColumnMap = snapShotBuilderData.convertDMLColumnDataMap();

        return this.columns.stream()
                .filter(entityColumn -> {
                    DMLColumnData persistenceColumn = snapShotColumnMap.get(entityColumn.getColumnName());
                    return !entityColumn.getColumnValue().equals(persistenceColumn.getColumnValue());
                })
                .toList();
    }

    public void changeColumns(List<DMLColumnData> columns) {
        this.columns = columns;
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

    private List<DMLColumnData> getEntityColumnData(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        List<DMLColumnData> DMLColumnDataList = new ArrayList<>();
        for (Field field : fields) {
            getEntityPrimaryKey(DMLColumnDataList, field);
            createDMLEntityColumnData(DMLColumnDataList, field);
        }
        return DMLColumnDataList;
    }

    private void getEntityPrimaryKey(List<DMLColumnData> DMLColumnDataList, Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            DMLColumnDataList.add(DMLColumnData.creatInstancePkColumn(field.getName(), field.getType()));
        }
    }

    private void createDMLEntityColumnData(List<DMLColumnData> DMLColumnDataList, Field field) {
        if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(OneToMany.class))
            return; // @Transient인 경우 검증하지 않
        // @Transient인 경우 검증하지 않음
        String columnName = field.getName();

        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }

        DMLColumnDataList.add(DMLColumnData.createEntityColumn(columnName));
    }

    private <T> void createDMLInstanceColumnData(List<DMLColumnData> DMLColumnDataList, Field field, T entityInstance) {
        if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(OneToMany.class))
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
}
