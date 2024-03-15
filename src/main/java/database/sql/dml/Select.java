package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.part.WhereClause;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Select {
    private final String tableName;
    private final List<String> allColumnNames;
    private final String joinedAllColumnNames;

    public Select(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    private Select(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.allColumnNames = entityMetadata.getAllColumnNames();
        this.joinedAllColumnNames = entityMetadata.getJoinedAllColumnNames();
    }

    public String buildQuery(Map<String, Object> conditionMap) {
        StringJoiner query = new StringJoiner(" ")
                .add("SELECT")
                .add(joinedAllColumnNames)
                .add("FROM").add(tableName);
        String whereClause = whereClause(conditionMap);
        if (!whereClause.isEmpty()) {
            query.add(whereClause);
        }
        return query.toString();
    }

    public String buildQuery() {
        return buildQuery(Map.of());
    }

    private String whereClause(Map<String, Object> conditionMap) {
        return WhereClause.from(conditionMap, allColumnNames).toQuery();
    }
}
