package builder.dml;

import jakarta.persistence.Table;
import util.StringUtil;

public class JoinEntityData {

    private final String tableName;
    private final String joinColumnName;
    private final Object joinColumnValue;
    private final EntityColumn joinColumnData;
    private final String alias;
    private Class<?> clazz;

    public JoinEntityData(Class<?> clazz, String joinColumnName, Object joinColumnValue) {
        this.clazz = clazz;
        this.tableName = getTableName(clazz);
        this.joinColumnName = joinColumnName;
        this.joinColumnValue = joinColumnValue;
        this.joinColumnData = new EntityColumn(clazz);
        this.alias = QueryBuildUtil.getAlias(this.tableName);
    }

    public <T> JoinEntityData(T entityInstance, String joinColumnName, Object joinColumnValue) {
        this.clazz = entityInstance.getClass();
        this.tableName = getTableName(entityInstance.getClass());
        this.joinColumnName = joinColumnName;
        this.joinColumnValue = joinColumnValue;
        this.joinColumnData = new EntityColumn(entityInstance, entityInstance.getClass());
        this.alias = QueryBuildUtil.getAlias(this.tableName);
    }

    public EntityColumn getJoinColumnData() {
        return joinColumnData;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    public String getAlias() {
        return alias;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String wrapString() {
        return (this.joinColumnValue instanceof String) ? StringUtil.wrapSingleQuote(this.joinColumnValue) : String.valueOf(this.joinColumnValue);
    }

    private String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return table.name();
        }
        return entityClass.getSimpleName();
    }

}
