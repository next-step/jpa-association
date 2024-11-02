package builder.dml;

import util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityColumn extends BaseEntityColumn{

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

}
