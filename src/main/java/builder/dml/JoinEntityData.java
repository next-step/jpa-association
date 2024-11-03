package builder.dml;

import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

public class JoinEntityData {

    private final FetchType fetchType;
    private final String tableName;
    private final String joinColumnName;
    private final EntityColumn joinColumnData;
    private final String alias;

    public JoinEntityData(FetchType fetchType, Class<?> clazz, String joinColumnName) {
        this.fetchType = fetchType;
        this.tableName = getTableName(clazz);
        this.joinColumnName = joinColumnName;
        this.joinColumnData = new EntityColumn(clazz);
        this.alias = QueryBuildUtil.getAlias(this.tableName);
    }

    public <T> JoinEntityData(FetchType fetchType, T entityInstance, String joinColumnName) {
        this.fetchType = fetchType;
        this.tableName = getTableName(entityInstance.getClass());
        this.joinColumnName = joinColumnName;
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

    private String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return table.name();
        }
        return entityClass.getSimpleName();
    }

}
