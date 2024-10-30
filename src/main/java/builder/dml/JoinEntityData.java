package builder.dml;

import jakarta.persistence.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JoinEntityData {

    private final FetchType fetchType;
    private final String tableName;
    private final String joinColumnName;
    private final List<DMLColumnData> joinColumnData;

    public JoinEntityData(FetchType fetchType, Class<?> clazz, String joinColumnName) {
        this.fetchType = fetchType;
        this.tableName = getTableName(clazz);
        this.joinColumnName = joinColumnName;
        this.joinColumnData = getEntityColumnData(clazz);
    }

    public List<DMLColumnData> getJoinColumnData() {
        return joinColumnData;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getJoinColumnName() {
        return joinColumnName;
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
        if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Id.class))
            return; // @Transient인 경우 검증하지 않
        // @Transient인 경우 검증하지 않음
        String columnName = field.getName();

        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }

        DMLColumnDataList.add(DMLColumnData.createEntityColumn(columnName));
    }


    private String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return table.name();
        }
        return entityClass.getSimpleName();
    }

}
