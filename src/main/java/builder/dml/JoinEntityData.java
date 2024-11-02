package builder.dml;

import jakarta.persistence.FetchType;

import java.util.List;

public class JoinEntityData extends BaseEntityColumn{

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

}
