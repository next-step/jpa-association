package builder.dml;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import org.jetbrains.annotations.NotNull;
import util.StringUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityColumn {

    private static final String GET_FIELD_VALUE_ERROR_MESSAGE = "필드 값을 가져오는 중 에러가 발생했습니다.";
    private final static String PK_NOT_EXIST_MESSAGE = "PK 컬럼을 찾을 수 없습니다.";
    private final static String COMMA = ", ";
    private final static String EQUALS = "=";

    private List<DMLColumnData> columns;

    public EntityColumn(Object entityInstance, Class<?> clazz) {
        this.columns = getInstanceColumnData(entityInstance, clazz);
    }

    public EntityColumn(Class<?> clazz) {
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

    private <T> List<DMLColumnData> getInstanceColumnData(T entityInstance, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(this::checkSkipAnnotation)
                .map(field -> getDmlColumnData(field, entityInstance))
                .collect(Collectors.toList());
    }

    private List<DMLColumnData> getEntityColumnData(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(this::checkSkipAnnotation)
                .map(this::getDmlColumnData)
                .collect(Collectors.toList());
    }

    @NotNull
    private static <T> DMLColumnData getDmlColumnData(Field field, T entityInstance) {
        if (field.isAnnotationPresent(Id.class)) {
            field.setAccessible(true);
            try {
                return DMLColumnData.createInstancePkColumn(field.getName(), field.getType(), field.get(entityInstance));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
            }
        }
        return getColumnData(field, entityInstance);
    }

    @NotNull
    private DMLColumnData getDmlColumnData(Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            return DMLColumnData.createEntityPkColumn(field.getName(), field.getType());
        }
        return getColumnData(field);
    }

    @NotNull
    private static <T> DMLColumnData getColumnData(Field field, T entityInstance) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }
        field.setAccessible(true);
        try {
            return DMLColumnData.creatInstanceColumn(columnName, field.getType(), field.get(entityInstance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
        }
    }

    @NotNull
    private DMLColumnData getColumnData(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }
        return DMLColumnData.createEntityColumn(columnName);
    }

    private boolean checkSkipAnnotation(Field field) {
        return !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(OneToMany.class);
    }

    public String getColumnNames() {
        return this.columns.stream()
                .map(DMLColumnData::getColumnName)
                .collect(Collectors.joining(COMMA));
    }
}
